package fm.draw.over.recyclerview.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import fm.draw.over.recyclerview.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
    }


    private fun initRecyclerView() = binding.recyclerView.apply {
        layoutManager = GridLayoutManager(context, SPAN_COUNT)
        adapter = ImagesAdapter(context)
//        disableStatusBarOffset()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }


    companion object {
        private const val SPAN_COUNT = 2
    }
}