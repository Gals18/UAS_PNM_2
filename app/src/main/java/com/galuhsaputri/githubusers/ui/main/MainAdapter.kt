package com.galuhsaputri.githubusers.ui.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.galuhsaputri.githubusers.R
import com.galuhsaputri.githubusers.databinding.ItemRowUserBinding
import com.galuhsaputri.githubusers.ui.detail.UserDetailActivity
import com.galuhsaputri.core.domain.model.UserSearchItem
import com.galuhsaputri.core.utils.viewUtils.load
//pada kelas MainAdapter.kt digunakan untuk membuat adapter recycleView untuk
// tampilan data pencarian dari pengguna github.
class MainAdapter(val context: Context) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    private var items = mutableListOf<UserSearchItem>()
    private lateinit var mainActivity: MainActivity

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: ItemRowUserBinding = ItemRowUserBinding.bind(itemView)

        fun bind(data: UserSearchItem) {
            binding.apply {
                ivUser.load(data.avatarUrl)
                binding.txtUsername.text = data.login
            }
            with(itemView) {
                setOnClickListener {
                    context.startActivity(
                        Intent(context, UserDetailActivity::class.java).apply {
                            putExtra(UserDetailActivity.USERNAME_KEY, data.login)
                        }
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_row_user, viewGroup, false)
        )
    }

    fun setActivity(activity: MainActivity) {
        this.mainActivity = activity
    }

    fun setItems(data: MutableList<UserSearchItem>) {
        this.items = data
        notifyDataSetChanged()
    }

    fun clearItems() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}