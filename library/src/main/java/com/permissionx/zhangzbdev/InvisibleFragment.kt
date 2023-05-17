package com.permissionx.zhangzbdev

import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

//typealias 关键字可以用于给任意类型指定一个别名，比如将(Boolean, List<String>) -> Unit的别名指定成了PermissionCallback，
//这样就可以使用PermissionCallback来替代之前所有使用(Boolean, List<String>) -> Unit的地方
typealias PermissionCallback = (Boolean, List<String>) -> Unit


/**
 * 想要对运行时权限的API进行封装并不是一件容易的事，因为这个操作是有特定的上下文依赖的，一般需要在Activity 中接收onRequestPermissionsResult()方法的回调才行，所以不能简单地将整个操作封装到一个独立的类中。
 * 当然，受此限制，也衍生出了一些特别的解决方案，比如将运行时权限的操作封装到BaseActivity中，或者提供一个透明的Activity 来处理运行时权限等。
 * 这里并不准备使用以上几种方案，而是准备使用另外一种业内普遍比较认可的小技巧来进行实现。
 * 事实上，Google 在Fragment 中也提供了一份相同的API，使得我们在Fragment 中也能申请运行时权限。
 * 但不同的是，Fragment 并不像Activity 那样必须有界面，我们完全可以向Activity 中添加一个隐藏的Fragment ，然后在这个隐藏的Fragment 中对运行时权限的API进行封装。
 * 这是一种非常轻量级的做法，不用担心隐藏Fragment 会对Activity 的性能造成什么影响
 */
class InvisibleFragment : Fragment() {

    //callback变量作为运行时权限申请结果的回调通知方式，
    // 并将它声明成了一种函数类型变量，该函数类型接收Boolean和List<String>这两种类型的参数，并且没有返回值
    private var callback: PermissionCallback? = null

    fun requestNow(cb: PermissionCallback, vararg permissions: String) {
        callback = cb
        requestPermissions(permissions, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == 1) {
            //来记录所有被用户拒绝的权限
            val deniedList = ArrayList<String>()
            for ((index, result) in grantResults.withIndex()) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(permissions[index])
                }
            }

            //来标识是否所有申请的权限均已被授权
            val allGranted = deniedList.isEmpty()
            callback?.let { it(allGranted, deniedList) }
        }
    }
}