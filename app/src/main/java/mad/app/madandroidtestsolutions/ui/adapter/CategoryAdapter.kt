package mad.app.madandroidtestsolutions.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import mad.app.madandroidtestsolutions.databinding.CategoryItemBinding
import mad.app.madandroidtestsolutions.util.downloadImage
import mad.app.plptest.CategoryQuery
import mad.app.plptest.CategoryRootQuery
import mad.app.plptest.ProductQuery
import mad.app.plptest.fragment.ProductListFragment
import kotlin.reflect.KFunction1

class CategoryAdapter :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private lateinit var binding: CategoryItemBinding

    private val differCallback = object : DiffUtil.ItemCallback<CategoryQuery.Item?>() {
        override fun areItemsTheSame(
            oldItem: CategoryQuery.Item,
            newItem: CategoryQuery.Item,
        ): Boolean {
            return oldItem.productListFragment.uid == newItem.productListFragment.uid
        }

        override fun areContentsTheSame(
            oldItem: CategoryQuery.Item,
            newItem: CategoryQuery.Item,
        ): Boolean {
            return oldItem == newItem
        }
    }

     val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItems(item: CategoryQuery.Item?) {
            binding.apply {
                categoryIcon.downloadImage("")
                categoryItemName.text = item?.productListFragment?.name
                categoryItemDescription.text = item?.productListFragment?.brand
                categoryItemPrice.text = "R" +
                    item?.productListFragment?.price_range?.priceRangeFragment?.maximum_price?.final_price?.value.toString()
            }
            itemView.setOnClickListener {
                onItemClickListener?.let { item?.let { it1 -> it(it1) } }
            }
        }
    }

    private var onItemClickListener: ((CategoryQuery.Item) -> Unit)? = null

    fun setOnItemClickListener(listener: (CategoryQuery.Item) -> Unit) {
        onItemClickListener = listener
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(differ.currentList[position])
    }

    override fun getItemCount() = differ.currentList.size

}


