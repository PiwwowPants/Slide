package me.ccrama.redditslide.util

import android.app.Dialog
import android.app.DialogFragment
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.util.SparseIntArray
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.AlertDialogWrapper
import me.ccrama.redditslide.Activities.Profile
import me.ccrama.redditslide.Activities.Website
import me.ccrama.redditslide.ColorPreferences
import me.ccrama.redditslide.OpenRedditLink
import me.ccrama.redditslide.R
import me.ccrama.redditslide.Reddit
import me.ccrama.redditslide.Visuals.Palette
import net.dean.jraw.models.Comment
import org.apache.commons.lang3.StringEscapeUtils

class BottomSheetFragmentHelper : BottomSheetDialogFragment() {
    val layout = LinearLayout(Reddit.getAppContext()).apply {
        layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    orientation = LinearLayout.VERTICAL
                }
        setPadding(16, 16, 16, 16)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(
            DialogFragment.STYLE_NORMAL,
            bottomSheetThemeSparseArray.get(ColorPreferences(context).fontStyle.themeType)
        )
        return super.onCreateDialog(savedInstanceState).apply {
            setTitle(arguments?.getString(BOTTOM_SHEET_TITLE_KEY) ?: "")
            setContentView(layout)
        }
    }

    fun addOpenExternally(url: String) {
        layout.addView(buildView(R.drawable.ic_open_in_browser, R.string.submission_link_extern).also {
            setOnClickListener(it, View.OnClickListener { LinkUtil.openExternally(url) })
        })
    }

    fun addShareText(title: String = "", text: String) {
        layout.addView(buildView(R.drawable.ic_share, R.string.share_link).also {
            setOnClickListener(it, View.OnClickListener { Reddit.defaultShareText(title, text, context) })
        })
    }

    fun addCopyUrl(url: String) {
        layout.addView(buildView(R.drawable.ic_content_copy, R.string.submission_link_copy).also {
            setOnClickListener(it, View.OnClickListener { LinkUtil.copyUrl(url, context) })
        })
    }

    fun addShareImage(url: String) {
        layout.addView(buildView(R.drawable.image, R.string.share_image).also {
            setOnClickListener(it, View.OnClickListener { Reddit.defaultShareText("", url, context) })
        })
    }

    fun addSaveImage(onClickListener: View.OnClickListener) {
        layout.addView(buildView(R.drawable.save, R.string.submission_save_image).also {
            setOnClickListener(it, View.OnClickListener { onClickListener.onClick(it) })
        })
    }

    fun addProfile(user: String) {
        layout.addView(buildView(R.drawable.profile, "/u/".plus(user)).also {
            setOnClickListener(it, View.OnClickListener {
                val i = Intent(context, Profile::class.java)
                i.putExtra(Profile.EXTRA_PROFILE, user)
                context!!.startActivity(i)
            })
        })
    }

    fun addSave(isSaved: Boolean, onClickListener: View.OnClickListener) {
        layout.addView(
            buildView(
                R.drawable.iconstarfilled,
                if (isSaved) R.string.comment_unsave else R.string.btn_save
            ).also {
                setOnClickListener(it, onClickListener)
            })
    }

    fun addDisableReplies(onClickListener: View.OnClickListener) {
        layout.addView(buildView(R.drawable.notif, R.string.disable_replies_comment).also {
            setOnClickListener(it, onClickListener)
        })
    }

    fun addReport(onClickListener: View.OnClickListener) {
        layout.addView(buildView(R.drawable.report, R.string.btn_report).also {
            setOnClickListener(it, onClickListener)
        })
    }

    fun addGild(link: String, comment: Comment) {
        layout.addView(buildView(R.drawable.gild, R.string.comment_gild).also {
            setOnClickListener(it, View.OnClickListener {
                context!!.startActivity(Intent(context, Website::class.java).apply {
                    putExtra(
                        Website.EXTRA_URL, "https://reddit.com"
                                + link
                                + comment.fullName.substring(3, comment.fullName.length)
                                + "?context=3&inapp=false"
                    )
                    putExtra(Website.EXTRA_COLOR, Palette.getColor(comment.subredditName))
                })
            })
        })
    }

    fun addCopyText(text: CharSequence) {
        layout.addView(buildView(R.drawable.ic_content_copy, R.string.misc_copy_text).also {
            setOnClickListener(it, View.OnClickListener {
                val showText = TextView(context).apply {
                    this.text = StringEscapeUtils.unescapeHtml4(text.toString())
                    setTextIsSelectable(true)
                    val sixteen = Reddit.dpToPxVertical(24)
                    setPadding(sixteen, 0, sixteen, 0)
                }
                AlertDialogWrapper.Builder(context!!).apply {
                    setView(showText)
                    setTitle("Select text to copy")
                    setCancelable(true)
                    val clipboard = Reddit.getAppContext().getSystemService(
                        Context.CLIPBOARD_SERVICE
                    ) as ClipboardManager
                    setPositiveButton("COPY") { _, _ ->
                        clipboard.primaryClip = ClipData.newPlainText(
                            "Comment text", showText.text
                                .toString()
                                .substring(
                                    showText.selectionStart,
                                    showText.selectionEnd
                                )
                        )

                        Toast.makeText(
                            Reddit.getAppContext(), R.string.submission_comment_copied,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    setNegativeButton(R.string.btn_cancel, null)
                    setNeutralButton(
                        "COPY ALL"
                    ) { _, _ ->
                        clipboard.primaryClip = ClipData.newPlainText(
                            "Comment text",
                            text.toString()
                        )

                        Toast.makeText(
                            Reddit.getAppContext(),
                            R.string.submission_comment_copied,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }.show()
            })
        })
    }

    fun addPermalink(link: String, comment: Comment) {
        layout.addView(buildView(R.drawable.link, R.string.comment_permalink).also {
            setOnClickListener(it, View.OnClickListener {
                OpenRedditLink(
                    context, "https://reddit.com"
                        .plus(link)
                        .plus(comment.fullName.substring(3, comment.fullName.length))
                        .plus("?context=3")
                )
            })
        })
    }

    fun addShareComment(link: String, comment: Comment, title: String) {
        addShareText(
            title, "https://reddit.com"
                .plus(link)
                .plus(comment.fullName.substring(3, comment.fullName.length))
                .plus("?context=3")
        )
    }

    fun addShowParentComment(onClickListener: View.OnClickListener) {
        layout.addView(buildView(R.drawable.commentchange, R.string.comment_parent).also {
            setOnClickListener(it, onClickListener)
        })
    }

    private fun setOnClickListener(view: View, onClickListener: View.OnClickListener) {
        view.setOnClickListener({
            onClickListener.onClick(view)
            this@BottomSheetFragmentHelper.dismiss()
        })
    }

    private fun buildView(iconRes: Int, text: String): View {
        return LinearLayout(Reddit.getAppContext()).apply {
            layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        .apply {
                            orientation = LinearLayout.HORIZONTAL
                        }
            setPadding(8, 16, 8, 16)
            addView(TextView(Reddit.getAppContext()).apply {
                setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0)
                compoundDrawablePadding = 16
                setText(text)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                }
                textSize = 20F
            })
        }
    }

    private fun buildView(iconRes: Int, stringRes: Int): View =
        buildView(iconRes, Reddit.getAppContext().getString(stringRes))

    companion object {
        private val bottomSheetThemeSparseArray = SparseIntArray().apply {
            put(ColorPreferences.ColorThemeOptions.Dark.value, R.style.BottomSheet_StyleDialog_Dark)
            put(ColorPreferences.ColorThemeOptions.Light.value, R.style.BottomSheet_StyleDialog_Light)
            put(ColorPreferences.ColorThemeOptions.AMOLED.value, R.style.BottomSheet_StyleDialog_AMOLED)
            put(ColorPreferences.ColorThemeOptions.DarkBlue.value, R.style.BottomSheet_StyleDialog_Dark_Blue)
            put(ColorPreferences.ColorThemeOptions.AMOLEDContrast.value, R.style.BottomSheet_StyleDialog_AMOLED)
            put(ColorPreferences.ColorThemeOptions.Sepia.value, R.style.BottomSheet_StyleDialog_Sepia)
            put(ColorPreferences.ColorThemeOptions.RedShift.value, R.style.BottomSheet_StyleDialog_Night_Red)
            put(ColorPreferences.ColorThemeOptions.Pixel.value, R.style.BottomSheet_StyleDialog_PIXEL)
            put(ColorPreferences.ColorThemeOptions.Deep.value, R.style.BottomSheet_StyleDialog_Deep)
        }
        private const val BOTTOM_SHEET_TITLE_KEY = "bottomSheetTitle"
        fun newInstance(title: CharSequence): BottomSheetFragmentHelper = BottomSheetFragmentHelper().apply {
            arguments = Bundle().apply {
                putString(BOTTOM_SHEET_TITLE_KEY, title.toString())
            }
        }
    }
}
