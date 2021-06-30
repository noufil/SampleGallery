package com.example.samplegallery.home.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.samplegallery.R
import com.example.samplegallery.base.BaseViewHolder
import com.example.samplegallery.databinding.FragmentViewPagerBinding
import com.example.samplegallery.home.adapters.ImageListAdapter
import com.example.samplegallery.home.models.ImageSearchResponse
import com.example.samplegallery.home.viewModels.HomeViewModel
import kotlin.collections.set

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
const val ARGUMENT_INDEX = "index"

class ViewPagerFragment : Fragment() {

    val viewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentViewPagerBinding? = null
    private lateinit var listAdapter: ImageListAdapter
    var index: Int = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireArguments().let {
            index = it.getInt(ARGUMENT_INDEX, 0)
        }
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        prepareSharedElementTransition()
        postponeEnterTransition()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listAdapter = ImageListAdapter(viewModel, null)
        binding.viewpager.apply {
            adapter = listAdapter
            listAdapter.updateData(viewModel.searchResponse.value!!)
            setCurrentItem(index, false)
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    viewModel.currentPosition = position
                }
            })
        }
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

    private fun prepareSharedElementTransition() {
        sharedElementEnterTransition =
            TransitionInflater.from(context)
                .inflateTransition(R.transition.image_shared_element_transition)

        setEnterSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String>,
                    sharedElements: MutableMap<String, View>
                ) {
                    val viewHolder =
                        (binding.viewpager[0] as RecyclerView).findViewHolderForAdapterPosition(
                            viewModel.currentPosition
                        ) as BaseViewHolder? ?: return
                    sharedElements[names[0]] = viewHolder.itemView
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}