package com.cuneytayyildiz.githubissueexplorer.utils.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.net.ConnectivityManager
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cuneytayyildiz.githubissueexplorer.R
import com.cuneytayyildiz.githubissueexplorer.utils.CircleTransformation
import com.cuneytayyildiz.githubissueexplorer.utils.TimeAgo
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import okhttp3.ResponseBody
import org.json.JSONObject
import java.text.SimpleDateFormat

inline fun <reified T : ViewModel> FragmentActivity.createViewModel(
    factory: ViewModelProvider.Factory,
    body: T.() -> Unit
): T {
    val viewModel = ViewModelProviders.of(this, factory).get(T::class.java)
    viewModel.body()
    return viewModel
}

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) {
    liveData.observe(this, Observer(body))
}

inline fun <reified T : ViewModel> Fragment.createViewModel(
    factory: ViewModelProvider.Factory,
    body: T.() -> Unit
): T {
    activity?.let {
        return it.createViewModel(factory, body)
    }
}

fun AppCompatActivity.replaceFragmentInActivity(fragment: Fragment, @IdRes frameId: Int) {
    supportFragmentManager.transact {
        replace(frameId, fragment)
    }
}

private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}

fun AppCompatActivity.isConnectedToInternet(): Boolean {
    val connectivity = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    connectivity.allNetworkInfo?.let { networkInfo ->
        networkInfo.forEach {
            if (it.isConnected) return true
        }
    }
    return false
}

fun Context.color(@ColorRes color: Int): Int {
    return ContextCompat.getColor(this, color)
}

fun String.timeAgo(): String? {
    try {
        val formatInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return TimeAgo.getTimeAgo(formatInput.parse(this).time)
    } catch (e: Exception) {
    }
    return this
}

fun Activity.hideKeyboard() {
    hideKeyboard(if (currentFocus == null) View(this) else currentFocus)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


fun <T, R> T.otherwise(block: T.() -> R) = run(block)

fun ImageView.load(avatarUrl: String?) {
    avatarUrl?.let {
        if (it.isNotEmpty()) {
            Picasso.get().load(it).fit()
                .transform(CircleTransformation())
                .placeholder(R.drawable.bg_placeholder)
                .into(this)
        }
    }
}

fun ResponseBody.message(): String {
    val jObjError = JSONObject(this.string())
    return jObjError.getString("message") ?: toString()
}

fun View.snack(text: String, retry: (Snackbar) -> Unit) {
    val snackbar = Snackbar.make(this, text, Snackbar.LENGTH_INDEFINITE)
    snackbar.setAction(R.string.button_retry) { retry(snackbar) }
    snackbar.show()
}


fun SwipeRefreshLayout.showLoading() {
    if (!isRefreshing) {
        post { isRefreshing = true }
    }
}

fun SwipeRefreshLayout.hideLoading() {
    if (isRefreshing) {
        post { isRefreshing = false }
    }
}


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun EditText.onDoneAction(action: (String) -> Unit) {
    this.setOnEditorActionListener { v, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            clearFocus()

            hideKeyboard()

            text?.toString()?.let { action(it) }

            return@setOnEditorActionListener true
        }
        return@setOnEditorActionListener false
    }
}

fun EditText.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun <T> Spinner.afterItemSelected(onItemSelected: (T) -> Unit) {
    object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {

        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            Log.e("afterItemSelected", parent?.adapter?.getItem(position).toString())
            onItemSelected.invoke(parent?.adapter?.getItem(position) as T)
        }

    }
}

fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()

fun AppCompatActivity.keyboardVisibilityChanges(): Observable<Boolean> {

    // flag indicates whether keyboard is open
    var isKeyboardOpen = false

    val notifier: BehaviorSubject<Boolean> = BehaviorSubject.create()

    // approximate keyboard height
    val approximateKeyboardHeight = dip(100)

    // device screen height
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    val dm = DisplayMetrics()
    wm.defaultDisplay.getMetrics(dm)

    val screenHeight: Int = dm.heightPixels

    val visibleDisplayFrame = Rect()

    val viewTreeObserver = window.decorView.viewTreeObserver

    val onDrawListener = ViewTreeObserver.OnDrawListener {

        window.decorView.getWindowVisibleDisplayFrame(visibleDisplayFrame)

        val keyboardHeight = screenHeight - (visibleDisplayFrame.bottom - visibleDisplayFrame.top)

        val keyboardOpen = keyboardHeight >= approximateKeyboardHeight

        val hasChanged = isKeyboardOpen xor keyboardOpen

        if (hasChanged) {
            isKeyboardOpen = keyboardOpen
            notifier.onNext(keyboardOpen)
        }
    }

    val lifeCycleObserver = object : GenericLifecycleObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event?) {
            if (source.lifecycle.currentState == Lifecycle.State.DESTROYED) {
                viewTreeObserver.removeOnDrawListener(onDrawListener)
                source.lifecycle.removeObserver(this)
                notifier.onComplete()
            }
        }
    }

    viewTreeObserver.addOnDrawListener(onDrawListener)
    lifecycle.addObserver(lifeCycleObserver)

    return notifier
        .doOnDispose {
            viewTreeObserver.removeOnDrawListener(onDrawListener)
            lifecycle.removeObserver(lifeCycleObserver)
        }
        .onTerminateDetach()
        .hide()
}