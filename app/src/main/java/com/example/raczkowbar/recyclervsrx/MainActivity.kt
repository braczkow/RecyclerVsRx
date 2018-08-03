package com.example.raczkowbar.recyclervsrx

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    class NumbersItem(i: Int) {
        var ints: MutableList<Int> = mutableListOf()

        init {
            ints.add(i)
        }

        override fun toString(): String {
            var s = ""

            for (i in ints) {
                s += "$i "
            }

            return s
        }

    }

    class NumbersAdapter(val items: List<NumbersItem>) : RecyclerView.Adapter<NumbersAdapter.ViewHoler>() {
        val rand = Random(System.currentTimeMillis())


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHoler {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_number, parent, false)

            return ViewHoler(v)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onViewRecycled(holder: ViewHoler) {
            super.onViewRecycled(holder)

            holder.disposable.dispose()
        }

        override fun onBindViewHolder(holder: ViewHoler, position: Int) {
            var item = items.get(position)

            holder.text.setText(item.toString())
            holder.disposable = Observable.timer(rand.nextInt(3) + 1L, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<Long>() {
                        override fun onComplete() {
                        }

                        override fun onNext(t: Long) {
                            item.ints.add(item.ints.last())

                            holder.text.setText(item.toString())
                        }

                        override fun onError(e: Throwable) {
                        }

                    });
        }

        class ViewHoler(itemView: View?) : RecyclerView.ViewHolder(itemView) {
            var text: TextView
            lateinit var disposable: DisposableObserver<Long>

            init {
                text = itemView?.findViewById<TextView>(R.id.item_text)!!
            }
        }

    }


    private lateinit var adapter: NumbersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var items: MutableList<NumbersItem> = mutableListOf()
        for (i in 0..49) {
            items.add(NumbersItem(i))
        }

        adapter = NumbersAdapter(items)
        main_recycler.adapter = adapter
        main_recycler.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)

    }


}
