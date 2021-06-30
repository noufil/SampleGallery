package com.example.samplegallery.home.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.app.SharedElementCallback
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.samplegallery.R
import com.example.samplegallery.base.BaseViewHolder
import com.example.samplegallery.databinding.FragmentImageListBinding
import com.example.samplegallery.home.adapters.ImageListAdapter
import com.example.samplegallery.home.models.ImageSearchResponse
import com.example.samplegallery.home.viewModels.HomeViewModel
import com.example.samplegallery.utils.hideKeypad
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.set

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ImageListFragment : Fragment() {

    val viewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentImageListBinding? = null
    private lateinit var listAdapter: ImageListAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setHasOptionsMenu(true)
        _binding = FragmentImageListBinding.inflate(inflater, container, false)
        exitTransition =
            TransitionInflater.from(context).inflateTransition(R.transition.grid_exit_transition)
        postponeEnterTransition()
        prepareSharedElementTransition()
        return binding.root

    }

    private fun prepareSharedElementTransition() {
        // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
        setEnterSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String>,
                    sharedElements: MutableMap<String, View>
                ) {
                    // Locate the image view at the primary fragment (the ImageFragment that is currently
                    // visible). To locate the fragment, call instantiateItem with the selection position.
                    // At this stage, the method will simply return the fragment at the position and will
                    // not create a new one.
                    val viewHolder =
                        binding.recyclerView.findViewHolderForAdapterPosition(viewModel.currentPosition) as BaseViewHolder?
                            ?: return

                    // Map the first shared element name to the child ImageView.
                    sharedElements[names[0]] = viewHolder.itemView
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
        val menuItem = menu.findItem(R.id.search)
        (menuItem.actionView as SearchView).let { searchView ->
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!query.isNullOrEmpty() && query != viewModel.previousQuery) {
                        viewModel.searchData(query, 1)
                        if (!searchView.isIconified) {
                            searchView.isIconified = true
                        }
                        searchView.onActionViewCollapsed()
                        searchView.hideKeypad()
                        menuItem.collapseActionView()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.twoColumns -> changeColumns(2)
            R.id.threeColumns ->
                changeColumns(3)
            R.id.fourColumns ->
                changeColumns(4)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeColumns(columns: Int) {
        viewModel.columns = columns
        listAdapter.columns = columns
        (binding.recyclerView.layoutManager as GridLayoutManager).spanCount = columns
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listAdapter =
            ImageListAdapter(viewModel, itemClickListener).apply { columns = viewModel.columns }
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, viewModel.columns)
            adapter = listAdapter
        }
        scrollToPosition()
        viewModel.searchResponse.observe(
            viewLifecycleOwner, { imageSearchResponse: ImageSearchResponse? ->
                if (imageSearchResponse == null)
                    listAdapter.reset()
                else
                    listAdapter.updateData(imageSearchResponse)
            })

        (view.parent as? ViewGroup)?.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun scrollToPosition() {
        val recyclerView = binding.recyclerView
        recyclerView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                recyclerView.removeOnLayoutChangeListener(this)
                val layoutManager: RecyclerView.LayoutManager = recyclerView.layoutManager!!
                val viewAtPosition = layoutManager.findViewByPosition(viewModel.currentPosition)
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null
                    || layoutManager.isViewPartiallyVisible(viewAtPosition, false, true)
                ) {
                    recyclerView.post { layoutManager.scrollToPosition(viewModel.currentPosition) }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val itemClickListener = View.OnClickListener { view ->
        val index = view.getTag(R.id.index) as Int
        viewModel.currentPosition = index

        val fragmentNavigatorExtras = FragmentNavigatorExtras(
            view to view.transitionName
        )
        val bundle = bundleOf(
            ARGUMENT_INDEX to index,
        )
        findNavController().navigate(R.id.action_viewpager, bundle, null, fragmentNavigatorExtras)
    }
}