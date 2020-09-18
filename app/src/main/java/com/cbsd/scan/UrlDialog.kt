package com.cbsd.scan

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.dialog_bottom_edit.*

class UrlDialog(context: Context, themeResId: Int) : Dialog(context, themeResId) {
    private var onUrlDialogEditChangedListener: OnUrlDialogEditChangedListener? = null
    private var title: String? = null
    private var hint: String? = null
    private var mContext: Context? = null

    constructor(context: Context) : this(context, R.style.BottomEditDialogStyle) {
        mContext = context
        initView()
    }

    private fun initView() {
        initDialog()
        val contentView: View = layoutInflater.inflate(R.layout.dialog_bottom_edit, null, false)
        val display = (mContext as Activity?)!!.windowManager.defaultDisplay
        val point = Point()
        display.getSize(point)
        contentView.minimumWidth = point.x - 100
        setContentView(contentView)
        setTitle(title)
        setHint(hint)

        dialogEditCancelBtn.setOnClickListener {
            dismiss()
        }
        dialogEditTestBtn.setOnClickListener {
            val content = dialogEditContentEt!!.text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(content)) {
                if (content.startsWith("http://") || content.startsWith("https://")) {
                    if (onUrlDialogEditChangedListener != null) onUrlDialogEditChangedListener!!.onEditTest(
                        content
                    )
                }else
                    Toast.makeText(context, "url格式错误", Toast.LENGTH_SHORT).show()

            } else
                Toast.makeText(context, "url不能为空", Toast.LENGTH_SHORT).show()
        }
        dialogEditConfirmBtn.setOnClickListener {
            val content = dialogEditContentEt!!.text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(content)) {
                if (content.startsWith("http://") || content.startsWith("https://")) {
                    if (onUrlDialogEditChangedListener != null) onUrlDialogEditChangedListener!!.onEditChanged(
                        content
                    )
                    dismiss()
                } else
                    Toast.makeText(context, "url格式错误", Toast.LENGTH_SHORT).show()

            } else
                Toast.makeText(context, "url不能为空", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initDialog() {
        window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    mContext!!,
                    R.color.transparent
                )
            )
        )
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    fun setTitle(title: String?) {
        this.title = title
        if (dialogEditTitleTv != null && !TextUtils.isEmpty(this.title)) {
            dialogEditTitleTv!!.text = this.title
        }
    }

    fun setHint(hint: String?) {
        this.hint = hint
        if (dialogEditContentEt != null && !TextUtils.isEmpty(this.hint)) {
            dialogEditContentEt!!.hint = this.hint
        }
    }

    fun setOnBottomDialogEditChangedListener(
        changed: (url: String?) -> Unit,
        test: (url: String?) -> Unit
    ) {

        onUrlDialogEditChangedListener = object : OnUrlDialogEditChangedListener {
            override fun onEditChanged(content: String?) {
                changed(content)
            }

            override fun onEditTest(content: String?) {
                test(content)
            }
        }
    }

    interface OnUrlDialogEditChangedListener {
        fun onEditChanged(content: String?)
        fun onEditTest(content: String?)
    }
}