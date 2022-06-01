package com.example.myapplication.ui.recommandMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.FragmentRecommandBinding
import com.example.myapplication.ui.recommandMenu.model.Food
import com.example.myapplication.ui.recommandMenu.model.People
import com.example.myapplication.ui.recommandMenu.model.SampleData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


class RecommandFragment : Fragment() {

    private var _binding: FragmentRecommandBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.

    private var selectFoodOption = Food.ALL
    private var selectPeopleOption = People.ALL
    private fun getRandomRange(until : Int) = Random.nextInt(until)

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecommandBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as MainActivity).bottomNavigationShow(false)//bottom네비게이션 없애기

        binding.groupFoodType.setOnClickedButtonListener { _, position ->
            selectFoodOption = enumValues<Food>().first { it.index == position }

        }

        binding.groupPeopleType.setOnClickedButtonListener { _, position ->
            selectPeopleOption = enumValues<People>().first { it.index == position }
        }

        binding.btnRandom.setOnClickListener {

            lifecycleScope.launch {
                binding.progress.visibility = View.VISIBLE
                val list = SampleData.allFoodItems
                var result = ""
                if (selectFoodOption == Food.ALL && selectPeopleOption == People.ALL) {
                    //전체 all 이므로 전체를 보여줌
                    result = list[getRandomRange(list.size -1)].name
                } else if (selectFoodOption == Food.ALL || selectPeopleOption == People.ALL) {
                    //둘중하나가 all 일경우

                    if (selectPeopleOption == People.ALL) {
                        //사람이 all 이기떄문에 음식 타입만 체크해준다
                        val tempFilter = list.filter { it.food == selectFoodOption}
                        result = tempFilter[getRandomRange(tempFilter.size -1)].name
                    } else {
                        //음식이 all 이기떄문에 사람 타입만 체크해준다
                        val tempFilter = list.filter { it.people == selectPeopleOption }
                        result = tempFilter[getRandomRange(tempFilter.size -1)].name
                    }
                }else{
                    //둘다 all이 아니라 옵션이선택 되어있음
                    val tempFilter = list.filter { it.food == selectFoodOption && it.people == selectPeopleOption}
                    result = tempFilter[getRandomRange(tempFilter.size-1)].name
                }
                // 로딩딜레이 넣어줌.
                delay(500)
                binding.progress.visibility = View.INVISIBLE
                binding.result.text = result
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).bottomNavigationShow(true)
    }

}