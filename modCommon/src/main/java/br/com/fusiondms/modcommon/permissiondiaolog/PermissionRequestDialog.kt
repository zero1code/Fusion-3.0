package br.com.fusiondms.modcommon.permissiondiaolog

import android.app.Activity
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import br.com.fusiondms.modcommon.R
import br.com.fusiondms.modcommon.databinding.PermissionDialogBinding
import br.com.fusiondms.modcommon.setDefaultStatusBarColor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment



fun Activity.showPermissionRequestDialog() {
    val metrics = Resources.getSystem().displayMetrics
    val bottomSheetDialog = BottomSheetDialog(this)
    val inflater = this.layoutInflater
    val binding = PermissionDialogBinding.inflate(inflater)
    bottomSheetDialog.setContentView(binding.root)

    val bottomSheetBehavior = BottomSheetBehavior.from<View>(binding.root.parent as View)
    bottomSheetBehavior.apply {
        state = BottomSheetBehavior.STATE_EXPANDED
    }

    val coordinatorLayout = binding.bottomSheetLayout
    coordinatorLayout.minimumHeight = metrics.heightPixels

    bottomSheetDialog.show()
}

//class PermissionDialog() : BottomSheetDialogFragment() {
//
//    private var _binding: PermissionDialogBinding? = null
//    private val binding: PermissionDialogBinding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = PermissionDialogBinding.inflate(inflater)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val bottomSheet = view.parent as View
//        val metrics = Resources.getSystem().displayMetrics
//
//        val bottomSheetBehavior = BottomSheetBehavior.from<View>(bottomSheet)
//        bottomSheetBehavior.apply {
//            isFitToContents = false
//            isDraggable = false
//            this.state = BottomSheetBehavior.STATE_EXPANDED
//            peekHeight = metrics.heightPixels
//        }
//
//
//        val coordinatorLayout = binding.bottomSheetLayout
//        coordinatorLayout.minimumHeight = metrics.heightPixels
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//    }
//
//    companion object {
//        const val TAG = "PermissionDialog"
//    }
//}