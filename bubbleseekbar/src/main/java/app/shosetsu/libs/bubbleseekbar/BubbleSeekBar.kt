package app.shosetsu.libs.bubbleseekbar

import android.animation.*
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.*
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import java.math.BigDecimal
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * A beautiful and powerful Android custom seek bar, which has a bubble view with progress
 * appearing upon when seeking. Highly customizable, mostly demands has been considered.
 *
 *
 * Created by woxingxiao on 2016-10-27.
 */
class BubbleSeekBar @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
) : View(context, attrs) {
	@IntDef(NONE, TextPosition.SIDES, TextPosition.BOTTOM_SIDES, TextPosition.BELOW_SECTION_MARK)
	@Retention(AnnotationRetention.SOURCE)
	annotation class TextPosition {
		companion object {
			const val SIDES = 0
			const val BOTTOM_SIDES = 1
			const val BELOW_SECTION_MARK = 2
		}
	}

	var min // min
			: Float
		private set
	var max // max
			: Float
		private set
	private var mProgress // real time value
			: Float
	private var isFloatType // support for float type output
			: Boolean
	private var mTrackSize // height of right-track(on the right of thumb)
			: Int
	private var mSecondTrackSize // height of left-track(on the left of thumb)
			: Int
	private var mThumbRadius // radius of thumb
			: Int
	private var mThumbRadiusOnDragging // radius of thumb when be dragging
			: Int
	private var mTrackColor // color of right-track
			: Int
	private var mSecondTrackColor // color of left-track
			: Int
	private var mThumbColor // color of thumb
			: Int
	private var mSectionCount // shares of whole progress(max - min)
			: Int
	private var isShowSectionMark // show demarcation points or not
			: Boolean
	private var isAutoAdjustSectionMark // auto scroll to the nearest section_mark or not
			: Boolean
	private var isShowSectionText // show section-text or not
			: Boolean
	private var mSectionTextSize // text size of section-text
			: Int
	private var mSectionTextColor // text color of section-text
			: Int

	@TextPosition
	private var mSectionTextPosition = NONE // text position of section-text relative to track
	private var mSectionTextInterval // the interval of two section-text
			: Int
	private var isShowThumbText // show real time progress-text under thumb or not
			: Boolean
	private var mThumbTextSize // text size of progress-text
			: Int
	private var mThumbTextColor // text color of progress-text
			: Int
	private var isShowProgressInFloat // show bubble-progress in float or not
			: Boolean
	private var isTouchToSeek // touch anywhere on track to quickly seek
			: Boolean
	private var isSeekStepSection // seek one step by one section, the progress is discrete
			: Boolean
	private var isSeekBySection // seek by section, the progress may not be linear
			: Boolean
	private var mAnimDuration // duration of animation
			: Long
	private var isAlwaysShowBubble // bubble shows all time
			: Boolean
	private var mAlwaysShowBubbleDelay // the delay duration before bubble shows all the time
			: Long
	private var isHideBubble // hide bubble
			: Boolean
	private var isRtl // right to left
			: Boolean
	private var mBubbleColor // color of bubble
			: Int
	private var mBubbleTextSize // text size of bubble-progress
			: Int
	private var mBubbleTextColor // text color of bubble-progress
			: Int
	private var mDelta // max - min
			= 0f
	private var mSectionValue // (mDelta / mSectionCount)
			= 0f
	private var mThumbCenterX // X coordinate of thumb's center
			= 0f
	private var mTrackLength // pixel length of whole track
			= 0f
	private var mSectionOffset // pixel length of one section
			= 0f
	private var isThumbOnDragging // is thumb on dragging or not
			= false
	private val mTextSpace // space between text and track
			: Int
	private var triggerBubbleShowing = false
	private var mSectionTextArray = SparseArray<String?>()
	private var mPreThumbCenterX = 0f
	private var triggerSeekBySection = false
	var onProgressChangedListener // progress changing listener
			: OnProgressChangedListener? = null
	private var mLeft // space between left of track and left of the view
			= 0f
	private var mRight // space between right of track and left of the view
			= 0f
	private val mPaint: Paint
	private val mRectText: Rect
	private val mWindowManager: WindowManager?
	private val mBubbleView: BubbleView?
	private var mBubbleRadius = 0
	private var mBubbleCenterRawSolidX = 0f
	private var mBubbleCenterRawSolidY = 0f
	private var mBubbleCenterRawX = 0f
	private val mLayoutParams: WindowManager.LayoutParams?
	private val mPoint = IntArray(2)
	private var isTouchToSeekAnimEnd = true
	private var mPreSecValue // previous SectionValue
			= 0f
	private var mConfigBuilder // config attributes
			: BubbleConfigBuilder? = null

	private fun initConfigByPriority() {
		if (min == max) {
			min = 0.0f
			max = 100.0f
		}
		if (min > max) {
			val tmp = max
			max = min
			min = tmp
		}
		if (mProgress < min) {
			mProgress = min
		}
		if (mProgress > max) {
			mProgress = max
		}
		if (mSecondTrackSize < mTrackSize) {
			mSecondTrackSize = mTrackSize + BubbleUtils.dp2px(2)
		}
		if (mThumbRadius <= mSecondTrackSize) {
			mThumbRadius = mSecondTrackSize + BubbleUtils.dp2px(2)
		}
		if (mThumbRadiusOnDragging <= mSecondTrackSize) {
			mThumbRadiusOnDragging = mSecondTrackSize * 2
		}
		if (mSectionCount <= 0) {
			mSectionCount = 10
		}
		mDelta = max - min
		mSectionValue = mDelta / mSectionCount
		if (mSectionValue < 1) {
			isFloatType = true
		}
		if (isFloatType) {
			isShowProgressInFloat = true
		}
		if (mSectionTextPosition != NONE) {
			isShowSectionText = true
		}
		if (isShowSectionText) {
			if (mSectionTextPosition == NONE) {
				mSectionTextPosition = TextPosition.SIDES
			}
			if (mSectionTextPosition == TextPosition.BELOW_SECTION_MARK) {
				isShowSectionMark = true
			}
		}
		if (mSectionTextInterval < 1) {
			mSectionTextInterval = 1
		}
		initSectionTextArray()
		if (isSeekStepSection) {
			isSeekBySection = false
			isAutoAdjustSectionMark = false
		}
		if (isAutoAdjustSectionMark && !isShowSectionMark) {
			isAutoAdjustSectionMark = false
		}
		if (isSeekBySection) {
			mPreSecValue = min
			if (mProgress != min) {
				mPreSecValue = mSectionValue
			}
			isShowSectionMark = true
			isAutoAdjustSectionMark = true
		}
		if (isHideBubble) {
			isAlwaysShowBubble = false
		}
		if (isAlwaysShowBubble) {
			setProgress(mProgress)
		}
		mThumbTextSize =
			if (isFloatType || isSeekBySection || isShowSectionText && mSectionTextPosition ==
				TextPosition.BELOW_SECTION_MARK
			) mSectionTextSize else mThumbTextSize
	}

	/**
	 * Calculate radius of bubble according to the Min and the Max
	 */
	private fun calculateRadiusOfBubble() {
		mPaint.textSize = mBubbleTextSize.toFloat()

		// 计算滑到两端气泡里文字需要显示的宽度，比较取最大值为气泡的半径
		var text: String = if (isShowProgressInFloat) {
			float2String(if (isRtl) max else min)
		} else {
			if (isRtl) {
				if (isFloatType) float2String(max) else "${max.toInt()}"
			} else {
				if (isFloatType) float2String(min) else "${min.toInt()}"
			}
		}
		mPaint.getTextBounds(text, 0, text.length, mRectText)
		val w1 = mRectText.width() + mTextSpace * 2 shr 1
		text = if (isShowProgressInFloat) {
			float2String(if (isRtl) min else max)
		} else {
			if (isRtl) {
				if (isFloatType) float2String(min) else "${min.toInt()}"
			} else {
				if (isFloatType) float2String(max) else "${max.toInt()}"
			}
		}
		mPaint.getTextBounds(text, 0, text.length, mRectText)
		val w2 = mRectText.width() + mTextSpace * 2 shr 1
		mBubbleRadius = BubbleUtils.dp2px(14) // default 14dp
		val max = max(mBubbleRadius, max(w1, w2))
		mBubbleRadius = max + mTextSpace
	}

	private fun initSectionTextArray() {
		val ifBelowSection = mSectionTextPosition == TextPosition.BELOW_SECTION_MARK
		val ifInterval = mSectionTextInterval > 1 && mSectionCount % 2 == 0
		var sectionValue: Float
		for (i in 0..mSectionCount) {
			sectionValue = if (isRtl) max - mSectionValue * i else min + mSectionValue * i
			if (ifBelowSection) {
				if (ifInterval) {
					sectionValue = if (i % mSectionTextInterval == 0) {
						if (isRtl) max - mSectionValue * i else min + mSectionValue * i
					} else {
						continue
					}
				}
			} else {
				if (i != 0 && i != mSectionCount) {
					continue
				}
			}
			mSectionTextArray.put(
				i,
				if (isFloatType) float2String(sectionValue) else "${sectionValue.toInt()}"
			)
		}
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		var height = mThumbRadiusOnDragging * 2 // 默认高度为拖动时thumb圆的直径
		if (isShowThumbText) {
			mPaint.textSize = mThumbTextSize.toFloat()
			mPaint.getTextBounds(
				"j",
				0,
				1,
				mRectText
			) // j is the highest of all letters and numbers
			height += mRectText.height() // 如果显示实时进度，则原来基础上加上进度文字高度和间隔
		}
		if (isShowSectionText && mSectionTextPosition >= TextPosition.BOTTOM_SIDES) { // 如果Section值在track之下显示，比较取较大值
			mPaint.textSize = mSectionTextSize.toFloat()
			mPaint.getTextBounds("j", 0, 1, mRectText)
			height = max(height, mThumbRadiusOnDragging * 2 + mRectText.height())
		}
		height += mTextSpace * 2
		setMeasuredDimension(resolveSize(BubbleUtils.dp2px(180), widthMeasureSpec), height)
		mLeft = (paddingLeft + mThumbRadiusOnDragging).toFloat()
		mRight = (measuredWidth - paddingRight - mThumbRadiusOnDragging).toFloat()
		if (isShowSectionText) {
			mPaint.textSize = mSectionTextSize.toFloat()
			if (mSectionTextPosition == TextPosition.SIDES) {
				var text = mSectionTextArray[0]
				mPaint.getTextBounds(text, 0, text!!.length, mRectText)
				mLeft += (mRectText.width() + mTextSpace).toFloat()
				text = mSectionTextArray[mSectionCount]
				mPaint.getTextBounds(text, 0, text!!.length, mRectText)
				mRight -= (mRectText.width() + mTextSpace).toFloat()
			} else if (mSectionTextPosition >= TextPosition.BOTTOM_SIDES) {
				var text = mSectionTextArray[0]
				mPaint.getTextBounds(text, 0, text!!.length, mRectText)
				var max = max(mThumbRadiusOnDragging.toFloat(), mRectText.width() / 2f)
				mLeft = paddingLeft + max + mTextSpace
				text = mSectionTextArray[mSectionCount]
				mPaint.getTextBounds(text, 0, text!!.length, mRectText)
				max = max(mThumbRadiusOnDragging.toFloat(), mRectText.width() / 2f)
				mRight = measuredWidth - paddingRight - max - mTextSpace
			}
		} else if (isShowThumbText && mSectionTextPosition == NONE) {
			mPaint.textSize = mThumbTextSize.toFloat()
			var text = mSectionTextArray[0]
			mPaint.getTextBounds(text, 0, text!!.length, mRectText)
			var max = max(mThumbRadiusOnDragging.toFloat(), mRectText.width() / 2f)
			mLeft = paddingLeft + max + mTextSpace
			text = mSectionTextArray[mSectionCount]
			mPaint.getTextBounds(text, 0, text!!.length, mRectText)
			max = max(mThumbRadiusOnDragging.toFloat(), mRectText.width() / 2f)
			mRight = measuredWidth - paddingRight - max - mTextSpace
		}
		mTrackLength = mRight - mLeft
		mSectionOffset = mTrackLength * 1f / mSectionCount
		if (!isHideBubble) {
			mBubbleView!!.measure(widthMeasureSpec, heightMeasureSpec)
		}
	}

	override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
		super.onLayout(changed, left, top, right, bottom)
		if (!isHideBubble) {
			locatePositionInWindow()
		}
	}

	/**
	 * In fact there two parts of the BubbleSeeBar, they are the BubbleView and the SeekBar.
	 *
	 *
	 * The BubbleView is added to Window by the WindowManager, so the only connection between
	 * BubbleView and SeekBar is their origin raw coordinates on the screen.
	 *
	 *
	 * It's easy to compute the coordinates(mBubbleCenterRawSolidX, mBubbleCenterRawSolidY) of point
	 * when the Progress equals the Min. Then compute the pixel length increment when the Progress is
	 * changing, the result is mBubbleCenterRawX. At last the WindowManager calls updateViewLayout()
	 * to update the LayoutParameter.x of the BubbleView.
	 *
	 *
	 * 气泡BubbleView实际是通过WindowManager动态添加的一个视图，因此与SeekBar唯一的位置联系就是它们在屏幕上的
	 * 绝对坐标。
	 * 先计算进度mProgress为mMin时BubbleView的中心坐标（mBubbleCenterRawSolidX，mBubbleCenterRawSolidY），
	 * 然后根据进度来增量计算横坐标mBubbleCenterRawX，再动态设置LayoutParameter.x，就实现了气泡跟随滑动移动。
	 */
	private fun locatePositionInWindow() {
		getLocationInWindow(mPoint)
		val parent = parent
		if (parent is View && (parent as View).measuredWidth > 0) {
			mPoint[0] %= (parent as View).measuredWidth
		}
		mBubbleCenterRawSolidX = if (isRtl) {
			mPoint[0] + mRight - mBubbleView!!.measuredWidth / 2f
		} else {
			mPoint[0] + mLeft - mBubbleView!!.measuredWidth / 2f
		}
		mBubbleCenterRawX = calculateCenterRawXofBubbleView()
		mBubbleCenterRawSolidY = (mPoint[1] - mBubbleView.measuredHeight).toFloat()
		mBubbleCenterRawSolidY -= BubbleUtils.dp2px(24).toFloat()
		if (BubbleUtils.isMIUI) {
			mBubbleCenterRawSolidY -= BubbleUtils.dp2px(4).toFloat()
		}
		val context = context
		if (context is Activity) {
			val window = context.window
			if (window != null) {
				val flags = window.attributes.flags
				if (flags and WindowManager.LayoutParams.FLAG_FULLSCREEN != 0) {
					val res = Resources.getSystem()
					val id = res.getIdentifier("status_bar_height", "dimen", "android")
					mBubbleCenterRawSolidY += res.getDimensionPixelSize(id).toFloat()
				}
			}
		}
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		var xLeft = paddingLeft.toFloat()
		var xRight = (measuredWidth - paddingRight).toFloat()
		val yTop = (paddingTop + mThumbRadiusOnDragging).toFloat()

		// draw sectionText SIDES or BOTTOM_SIDES
		if (isShowSectionText) {
			mPaint.color = mSectionTextColor
			mPaint.textSize = mSectionTextSize.toFloat()
			mPaint.getTextBounds(
				"0123456789",
				0,
				"0123456789".length,
				mRectText
			) // compute solid height
			if (mSectionTextPosition == TextPosition.SIDES) {
				val y_ = yTop + mRectText.height() / 2f
				var text = mSectionTextArray[0]
				mPaint.getTextBounds(text, 0, text!!.length, mRectText)
				canvas.drawText(text, xLeft + mRectText.width() / 2f, y_, mPaint)
				xLeft += (mRectText.width() + mTextSpace).toFloat()
				text = mSectionTextArray[mSectionCount]
				mPaint.getTextBounds(text, 0, text!!.length, mRectText)
				canvas.drawText(text, xRight - (mRectText.width() + 0.5f) / 2f, y_, mPaint)
				xRight -= (mRectText.width() + mTextSpace).toFloat()
			} else if (mSectionTextPosition >= TextPosition.BOTTOM_SIDES) {
				var y_ = yTop + mThumbRadiusOnDragging + mTextSpace
				var text = mSectionTextArray[0]
				mPaint.getTextBounds(text, 0, text!!.length, mRectText)
				y_ += mRectText.height().toFloat()
				xLeft = mLeft
				if (mSectionTextPosition == TextPosition.BOTTOM_SIDES) {
					canvas.drawText(text, xLeft, y_, mPaint)
				}
				text = mSectionTextArray[mSectionCount]
				mPaint.getTextBounds(text, 0, text!!.length, mRectText)
				xRight = mRight
				if (mSectionTextPosition == TextPosition.BOTTOM_SIDES) {
					canvas.drawText(text, xRight, y_, mPaint)
				}
			}
		} else if (isShowThumbText && mSectionTextPosition == NONE) {
			xLeft = mLeft
			xRight = mRight
		}
		if (!isShowSectionText && !isShowThumbText || mSectionTextPosition == TextPosition.SIDES) {
			xLeft += mThumbRadiusOnDragging.toFloat()
			xRight -= mThumbRadiusOnDragging.toFloat()
		}
		val isShowTextBelowSectionMark = isShowSectionText && mSectionTextPosition ==
				TextPosition.BELOW_SECTION_MARK

		// draw sectionMark & sectionText BELOW_SECTION_MARK
		if (isShowTextBelowSectionMark || isShowSectionMark) {
			mPaint.textSize = mSectionTextSize.toFloat()
			mPaint.getTextBounds(
				"0123456789",
				0,
				"0123456789".length,
				mRectText
			) // compute solid height
			var x_: Float
			val y_ = yTop + mRectText.height() + mThumbRadiusOnDragging + mTextSpace
			val r = (mThumbRadiusOnDragging - BubbleUtils.dp2px(2)) / 2f
			val junction: Float = if (isRtl) {
				mRight - mTrackLength / mDelta * abs(mProgress - min)
			} else {
				mLeft + mTrackLength / mDelta * abs(mProgress - min)
			} // where secondTrack meets firstTrack
			for (i in 0..mSectionCount) {
				x_ = xLeft + i * mSectionOffset
				if (isRtl) {
					mPaint.color = if (x_ <= junction) mTrackColor else mSecondTrackColor
				} else {
					mPaint.color = if (x_ <= junction) mSecondTrackColor else mTrackColor
				}
				// sectionMark
				canvas.drawCircle(x_, yTop, r, mPaint)

				// sectionText belows section
				if (isShowTextBelowSectionMark) {
					mPaint.color = mSectionTextColor
					if (mSectionTextArray[i, null] != null) {
						canvas.drawText(mSectionTextArray[i]!!, x_, y_, mPaint)
					}
				}
			}
		}
		if (!isThumbOnDragging || isAlwaysShowBubble) {
			mThumbCenterX = if (isRtl) {
				xRight - mTrackLength / mDelta * (mProgress - min)
			} else {
				xLeft + mTrackLength / mDelta * (mProgress - min)
			}
		}

		// draw thumbText
		if (isShowThumbText && !isThumbOnDragging && isTouchToSeekAnimEnd) {
			mPaint.color = mThumbTextColor
			mPaint.textSize = mThumbTextSize.toFloat()
			mPaint.getTextBounds(
				"0123456789",
				0,
				"0123456789".length,
				mRectText
			) // compute solid height
			val y_ = yTop + mRectText.height() + mThumbRadiusOnDragging + mTextSpace
			if (isFloatType || isShowProgressInFloat && mSectionTextPosition == TextPosition.BOTTOM_SIDES && mProgress != min && mProgress != max) {
				canvas.drawText(progressFloat.toString(), mThumbCenterX, y_, mPaint)
			} else {
				canvas.drawText(progress.toString(), mThumbCenterX, y_, mPaint)
			}
		}

		// draw track
		mPaint.color = mSecondTrackColor
		mPaint.strokeWidth = mSecondTrackSize.toFloat()
		if (isRtl) {
			canvas.drawLine(xRight, yTop, mThumbCenterX, yTop, mPaint)
		} else {
			canvas.drawLine(xLeft, yTop, mThumbCenterX, yTop, mPaint)
		}

		// draw second track
		mPaint.color = mTrackColor
		mPaint.strokeWidth = mTrackSize.toFloat()
		if (isRtl) {
			canvas.drawLine(mThumbCenterX, yTop, xLeft, yTop, mPaint)
		} else {
			canvas.drawLine(mThumbCenterX, yTop, xRight, yTop, mPaint)
		}

		// draw thumb
		mPaint.color = mThumbColor
		canvas.drawCircle(
			mThumbCenterX,
			yTop,
			if (isThumbOnDragging) mThumbRadiusOnDragging.toFloat() else mThumbRadius.toFloat(),
			mPaint
		)
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		super.onSizeChanged(w, h, oldw, oldh)
		post { requestLayout() }
	}

	override fun onVisibilityChanged(changedView: View, visibility: Int) {
		if (isHideBubble || !isAlwaysShowBubble) return
		if (visibility != VISIBLE) {
			hideBubble()
		} else {
			if (triggerBubbleShowing) {
				showBubble()
			}
		}
		super.onVisibilityChanged(changedView, visibility)
	}

	override fun onDetachedFromWindow() {
		hideBubble()
		super.onDetachedFromWindow()
	}

	var dx = 0f
	override fun onTouchEvent(event: MotionEvent): Boolean {
		when (event.actionMasked) {
			MotionEvent.ACTION_DOWN -> {
				performClick()
				parent.requestDisallowInterceptTouchEvent(true)
				isThumbOnDragging = isThumbTouched(event)
				if (isThumbOnDragging) {
					if (isSeekBySection && !triggerSeekBySection) {
						triggerSeekBySection = true
					}
					if (isAlwaysShowBubble && !triggerBubbleShowing) {
						triggerBubbleShowing = true
					}
					if (!isHideBubble) {
						showBubble()
					}
					invalidate()
				} else if (isTouchToSeek && isTrackTouched(event)) {
					isThumbOnDragging = true
					if (isSeekBySection && !triggerSeekBySection) {
						triggerSeekBySection = true
					}
					if (isAlwaysShowBubble) {
						hideBubble()
						triggerBubbleShowing = true
					}
					if (isSeekStepSection) {
						mPreThumbCenterX = calThumbCxWhenSeekStepSection(event.x)
						mThumbCenterX = mPreThumbCenterX
					} else {
						mThumbCenterX = event.x
						if (mThumbCenterX < mLeft) {
							mThumbCenterX = mLeft
						}
						if (mThumbCenterX > mRight) {
							mThumbCenterX = mRight
						}
					}
					mProgress = calculateProgress()
					if (!isHideBubble) {
						mBubbleCenterRawX = calculateCenterRawXofBubbleView()
						showBubble()
					}
					invalidate()
				}
				dx = mThumbCenterX - event.x
			}
			MotionEvent.ACTION_MOVE -> if (isThumbOnDragging) {
				var flag = true
				if (isSeekStepSection) {
					val x = calThumbCxWhenSeekStepSection(event.x)
					if (x != mPreThumbCenterX) {
						mPreThumbCenterX = x
						mThumbCenterX = mPreThumbCenterX
					} else {
						flag = false
					}
				} else {
					mThumbCenterX = event.x + dx
					if (mThumbCenterX < mLeft) {
						mThumbCenterX = mLeft
					}
					if (mThumbCenterX > mRight) {
						mThumbCenterX = mRight
					}
				}
				if (flag) {
					mProgress = calculateProgress()
					if (!isHideBubble && mBubbleView!!.parent != null) {
						mBubbleCenterRawX = calculateCenterRawXofBubbleView()
						mLayoutParams?.x = (mBubbleCenterRawX + 0.5f).toInt()
						mWindowManager?.updateViewLayout(mBubbleView, mLayoutParams)
						mBubbleView.setProgressText(if (isShowProgressInFloat) progressFloat.toString() else progress.toString())
					} else {
						processProgress()
					}
					invalidate()
					if (onProgressChangedListener != null) {
						onProgressChangedListener!!.onProgressChanged(
							this,
							progress,
							progressFloat,
							true
						)
					}
				}
			}
			MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
				parent.requestDisallowInterceptTouchEvent(false)
				if (isAutoAdjustSectionMark) {
					if (isTouchToSeek) {
						postDelayed({
							isTouchToSeekAnimEnd = false
							autoAdjustSection()
						}, mAnimDuration)
					} else {
						autoAdjustSection()
					}
				} else if (isThumbOnDragging || isTouchToSeek) {
					if (isHideBubble) {
						animate()
							.setDuration(mAnimDuration)
							.setStartDelay(if (!isThumbOnDragging && isTouchToSeek) 300 else 0.toLong())
							.setListener(object : AnimatorListenerAdapter() {
								override fun onAnimationEnd(animation: Animator) {
									isThumbOnDragging = false
									invalidate()
								}

								override fun onAnimationCancel(animation: Animator) {
									isThumbOnDragging = false
									invalidate()
								}
							}).start()
					} else {
						postDelayed({
							mBubbleView!!.animate()
								.alpha(if (isAlwaysShowBubble) 1f else 0f)
								.setDuration(mAnimDuration)
								.setListener(object : AnimatorListenerAdapter() {
									override fun onAnimationEnd(animation: Animator) {
										if (!isAlwaysShowBubble) {
											hideBubble()
										}
										isThumbOnDragging = false
										invalidate()
									}

									override fun onAnimationCancel(animation: Animator) {
										if (!isAlwaysShowBubble) {
											hideBubble()
										}
										isThumbOnDragging = false
										invalidate()
									}
								}).start()
						}, mAnimDuration)
					}
				}
				if (onProgressChangedListener != null) {
					onProgressChangedListener!!.onProgressChanged(
						this,
						progress,
						progressFloat,
						true
					)
					onProgressChangedListener!!.getProgressOnActionUp(this, progress, progressFloat)
				}
			}
		}
		return isThumbOnDragging || isTouchToSeek || super.onTouchEvent(event)
	}

	/**
	 * Detect effective touch of thumb
	 */
	private fun isThumbTouched(event: MotionEvent): Boolean {
		if (!isEnabled) return false
		val distance = mTrackLength / mDelta * (mProgress - min)
		val x = if (isRtl) mRight - distance else mLeft + distance
		val y = measuredHeight / 2f
		return ((event.x - x) * (event.x - x) + (event.y - y) * (event.y - y)
				<= (mLeft + BubbleUtils.dp2px(8)) * (mLeft + BubbleUtils.dp2px(8)))
	}

	/**
	 * Detect effective touch of track
	 */
	private fun isTrackTouched(event: MotionEvent): Boolean {
		return isEnabled && event.x >= paddingLeft && event.x <= measuredWidth - paddingRight && event.y >= paddingTop && event.y <= measuredHeight - paddingBottom
	}

	/**
	 * If the thumb is being dragged, calculate the thumbCenterX when the seek_step_section is true.
	 */
	private fun calThumbCxWhenSeekStepSection(touchedX: Float): Float {
		if (touchedX <= mLeft) return mLeft
		if (touchedX >= mRight) return mRight
		var x = 0f
		var i = 0
		while (i <= mSectionCount) {
			x = i * mSectionOffset + mLeft
			if (x <= touchedX && touchedX - x <= mSectionOffset) {
				break
			}
			i++
		}
		return if (touchedX - x <= mSectionOffset / 2f) {
			x
		} else {
			(i + 1) * mSectionOffset + mLeft
		}
	}

	/**
	 * Auto scroll to the nearest section mark
	 */
	private fun autoAdjustSection() {
		var x = 0f
		var i = 0
		while (i <= mSectionCount) {
			x = i * mSectionOffset + mLeft
			if (x <= mThumbCenterX && mThumbCenterX - x <= mSectionOffset) {
				break
			}
			i++
		}
		val bigDecimal = BigDecimal.valueOf(mThumbCenterX.toDouble())
		val x_ = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).toFloat()
		val onSection = x_ == x // 就在section处，不作valueAnim，优化性能
		val animatorSet = AnimatorSet()
		var valueAnim: ValueAnimator? = null
		if (!onSection) {
			valueAnim = if (mThumbCenterX - x <= mSectionOffset / 2f) {
				ValueAnimator.ofFloat(mThumbCenterX, x)
			} else {
				ValueAnimator.ofFloat(mThumbCenterX, (i + 1) * mSectionOffset + mLeft)
			}
			valueAnim.interpolator = LinearInterpolator()
			valueAnim.addUpdateListener(AnimatorUpdateListener { animation ->
				mThumbCenterX = animation.animatedValue as Float
				mProgress = calculateProgress()
				if (!isHideBubble && mBubbleView!!.parent != null) {
					mBubbleCenterRawX = calculateCenterRawXofBubbleView()
					mLayoutParams?.x = (mBubbleCenterRawX + 0.5f).toInt()
					mWindowManager?.updateViewLayout(mBubbleView, mLayoutParams)
					mBubbleView.setProgressText(if (isShowProgressInFloat) progressFloat.toString() else progress.toString())
				} else {
					processProgress()
				}
				invalidate()
				if (onProgressChangedListener != null) {
					onProgressChangedListener!!.onProgressChanged(
						this@BubbleSeekBar, progress,
						progressFloat, true
					)
				}
			})
		}
		if (isHideBubble) {
			if (!onSection) {
				animatorSet.setDuration(mAnimDuration).playTogether(valueAnim)
			}
		} else {
			val alphaAnim: ObjectAnimator = ObjectAnimator.ofFloat(
				mBubbleView,
				ALPHA,
				if (isAlwaysShowBubble) 1f else 0f
			)
			if (onSection) {
				animatorSet.setDuration(mAnimDuration).play(alphaAnim)
			} else {
				animatorSet.setDuration(mAnimDuration).playTogether(valueAnim, alphaAnim)
			}
		}
		animatorSet.addListener(object : AnimatorListenerAdapter() {
			override fun onAnimationEnd(animation: Animator) {
				if (!isHideBubble && !isAlwaysShowBubble) {
					hideBubble()
				}
				mProgress = calculateProgress()
				isThumbOnDragging = false
				isTouchToSeekAnimEnd = true
				invalidate()
				if (onProgressChangedListener != null) {
					onProgressChangedListener!!.getProgressOnFinally(
						this@BubbleSeekBar, progress,
						progressFloat, true
					)
				}
			}

			override fun onAnimationCancel(animation: Animator) {
				if (!isHideBubble && !isAlwaysShowBubble) {
					hideBubble()
				}
				mProgress = calculateProgress()
				isThumbOnDragging = false
				isTouchToSeekAnimEnd = true
				invalidate()
			}
		})
		animatorSet.start()
	}

	/**
	 * Showing the Bubble depends the way that the WindowManager adds a Toast type view to the Window.
	 *
	 *
	 * 显示气泡
	 * 原理是利用WindowManager动态添加一个与Toast相同类型的BubbleView，消失时再移除
	 */
	private fun showBubble() {
		if (mBubbleView == null || mBubbleView.parent != null) {
			return
		}
		mLayoutParams?.x = (mBubbleCenterRawX + 0.5f).toInt()
		mLayoutParams?.y = (mBubbleCenterRawSolidY + 0.5f).toInt()
		mBubbleView.alpha = 0f
		mBubbleView.visibility = VISIBLE
		mBubbleView.animate().alpha(1f).setDuration(if (isTouchToSeek) 0 else mAnimDuration)
			.setListener(object : AnimatorListenerAdapter() {
				override fun onAnimationStart(animation: Animator) {
					mWindowManager?.addView(mBubbleView, mLayoutParams)
				}
			}).start()
		mBubbleView.setProgressText(if (isShowProgressInFloat) progressFloat.toString() else progress.toString())
	}

	/**
	 * The WindowManager removes the BubbleView from the Window.
	 */
	private fun hideBubble() {
		if (mBubbleView == null) return
		mBubbleView.visibility = GONE // 防闪烁
		if (mBubbleView.parent != null)
			mWindowManager?.removeViewImmediate(mBubbleView)

	}

	private fun float2String(value: Float): String {
		return formatFloat(value).toString()
	}

	private fun formatFloat(value: Float): Float {
		val bigDecimal = BigDecimal.valueOf(value.toDouble())
		return bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).toFloat()
	}

	private fun calculateCenterRawXofBubbleView(): Float {
		return if (isRtl) {
			mBubbleCenterRawSolidX - mTrackLength * (mProgress - min) / mDelta
		} else {
			mBubbleCenterRawSolidX + mTrackLength * (mProgress - min) / mDelta
		}
	}

	private fun calculateProgress(): Float {
		return if (isRtl) {
			(mRight - mThumbCenterX) * mDelta / mTrackLength + min
		} else {
			(mThumbCenterX - mLeft) * mDelta / mTrackLength + min
		}
	}
	/////// Api begins /////////////////////////////////////////////////////////////////////////////
	/**
	 * When BubbleSeekBar's parent view is scrollable, must listener to it's scrolling and call this
	 * method to correct the offsets.
	 */
	fun correctOffsetWhenContainerOnScrolling() {
		if (isHideBubble) return
		locatePositionInWindow()
		if (mBubbleView!!.parent != null) {
			if (isAlwaysShowBubble) {
				mLayoutParams?.y = (mBubbleCenterRawSolidY + 0.5f).toInt()
				mWindowManager?.updateViewLayout(mBubbleView, mLayoutParams)
			} else {
				postInvalidate()
			}
		}
	}

	fun setProgress(progress: Float) {
		mProgress = progress
		if (onProgressChangedListener != null) {
			onProgressChangedListener!!.onProgressChanged(
				this,
				progress.toInt(),
				progressFloat,
				false
			)
			onProgressChangedListener!!.getProgressOnFinally(
				this,
				progress.toInt(),
				progressFloat,
				false
			)
		}
		if (!isHideBubble) {
			mBubbleCenterRawX = calculateCenterRawXofBubbleView()
		}
		if (isAlwaysShowBubble) {
			hideBubble()
			postDelayed({
				showBubble()
				triggerBubbleShowing = true
			}, mAlwaysShowBubbleDelay)
		}
		if (isSeekBySection) {
			triggerSeekBySection = false
		}
		postInvalidate()
	}

	val progress: Int
		get() = processProgress().roundToInt()
	val progressFloat: Float
		get() = formatFloat(processProgress())

	private fun processProgress(): Float {
		val progress = mProgress
		if (isSeekBySection && triggerSeekBySection) {
			val half = mSectionValue / 2
			if (isTouchToSeek) {
				if (progress == min || progress == max) {
					return progress
				}
				var secValue: Float
				for (i in 0..mSectionCount) {
					secValue = i * mSectionValue
					if (secValue < progress && secValue + mSectionValue >= progress) {
						return if (secValue + half > progress) {
							secValue
						} else {
							secValue + mSectionValue
						}
					}
				}
			}
			return if (progress >= mPreSecValue) { // increasing
				if (progress >= mPreSecValue + half) {
					mPreSecValue += mSectionValue
					mPreSecValue
				} else {
					mPreSecValue
				}
			} else { // reducing
				if (progress >= mPreSecValue - half) {
					mPreSecValue
				} else {
					mPreSecValue -= mSectionValue
					mPreSecValue
				}
			}
		}
		return progress
	}

	fun setTrackColor(@ColorInt trackColor: Int) {
		if (mTrackColor != trackColor) {
			mTrackColor = trackColor
			invalidate()
		}
	}

	fun setSecondTrackColor(@ColorInt secondTrackColor: Int) {
		if (mSecondTrackColor != secondTrackColor) {
			mSecondTrackColor = secondTrackColor
			invalidate()
		}
	}

	fun setThumbColor(@ColorInt thumbColor: Int) {
		if (mThumbColor != thumbColor) {
			mThumbColor = thumbColor
			invalidate()
		}
	}

	fun setBubbleColor(@ColorInt bubbleColor: Int) {
		if (mBubbleColor != bubbleColor) {
			mBubbleColor = bubbleColor
			mBubbleView?.invalidate()
		}
	}

	fun setCustomSectionTextArray(customSectionTextArray: CustomSectionTextArray) {
		mSectionTextArray = customSectionTextArray.onCustomize(mSectionCount, mSectionTextArray)
		for (i in 0..mSectionCount) {
			if (mSectionTextArray[i] == null) {
				mSectionTextArray.put(i, "")
			}
		}
		isShowThumbText = false
		requestLayout()
		invalidate()
	}

	/////// Api ends ///////////////////////////////////////////////////////////////////////////////
	fun config(builder: BubbleConfigBuilder) {
		min = builder.min
		max = builder.max
		mProgress = builder.progress
		isFloatType = builder.isFloatType
		mTrackSize = builder.trackSize
		mSecondTrackSize = builder.secondTrackSize
		mThumbRadius = builder.thumbRadius
		mThumbRadiusOnDragging = builder.thumbRadiusOnDragging
		mTrackColor = builder.trackColor
		mSecondTrackColor = builder.secondTrackColor
		mThumbColor = builder.thumbColor
		mSectionCount = builder.sectionCount
		isShowSectionMark = builder.isShowSectionMark
		isAutoAdjustSectionMark = builder.isAutoAdjustSectionMark
		isShowSectionText = builder.isShowSectionText
		mSectionTextSize = builder.sectionTextSize
		mSectionTextColor = builder.sectionTextColor
		mSectionTextPosition = builder.sectionTextPosition
		mSectionTextInterval = builder.sectionTextInterval
		isShowThumbText = builder.isShowThumbText
		mThumbTextSize = builder.thumbTextSize
		mThumbTextColor = builder.thumbTextColor
		isShowProgressInFloat = builder.isShowProgressInFloat
		mAnimDuration = builder.animDuration
		isTouchToSeek = builder.isTouchToSeek
		isSeekStepSection = builder.isSeekStepSection
		isSeekBySection = builder.isSeekBySection
		mBubbleColor = builder.bubbleColor
		mBubbleTextSize = builder.bubbleTextSize
		mBubbleTextColor = builder.bubbleTextColor
		isAlwaysShowBubble = builder.isAlwaysShowBubble
		mAlwaysShowBubbleDelay = builder.alwaysShowBubbleDelay
		isHideBubble = builder.isHideBubble
		isRtl = builder.isRTL
		initConfigByPriority()
		calculateRadiusOfBubble()
		if (onProgressChangedListener != null) {
			onProgressChangedListener!!.onProgressChanged(this, progress, progressFloat, false)
			onProgressChangedListener!!.getProgressOnFinally(this, progress, progressFloat, false)
		}
		mConfigBuilder = null
		requestLayout()
	}

	val configBuilder: BubbleConfigBuilder
		get() {
			if (mConfigBuilder == null)
				mConfigBuilder = BubbleConfigBuilder(this)

			assert(mConfigBuilder != null)

			mConfigBuilder!!.min = this.min
			mConfigBuilder!!.max = this.max
			mConfigBuilder!!.progress = this.mProgress
			mConfigBuilder!!.isFloatType = this.isFloatType
			mConfigBuilder!!.trackSize = this.mTrackSize
			mConfigBuilder!!.secondTrackSize = this.mSecondTrackSize
			mConfigBuilder!!.thumbRadius = this.mThumbRadius
			mConfigBuilder!!.thumbRadiusOnDragging = this.mThumbRadiusOnDragging
			mConfigBuilder!!.trackColor = this.mTrackColor
			mConfigBuilder!!.secondTrackColor = this.mSecondTrackColor
			mConfigBuilder!!.thumbColor = this.mThumbColor
			mConfigBuilder!!.sectionCount = this.mSectionCount
			mConfigBuilder!!.isShowSectionMark = this.isShowSectionMark
			mConfigBuilder!!.isAutoAdjustSectionMark = this.isAutoAdjustSectionMark
			mConfigBuilder!!.isShowSectionText = this.isShowSectionText
			mConfigBuilder!!.sectionTextSize = this.mSectionTextSize
			mConfigBuilder!!.sectionTextColor = this.mSectionTextColor
			mConfigBuilder!!.sectionTextPosition = this.mSectionTextPosition
			mConfigBuilder!!.sectionTextInterval = this.mSectionTextInterval
			mConfigBuilder!!.isShowThumbText = this.isShowThumbText
			mConfigBuilder!!.thumbTextSize = this.mThumbTextSize
			mConfigBuilder!!.thumbTextColor = this.mThumbTextColor
			mConfigBuilder!!.isShowProgressInFloat = this.isShowProgressInFloat
			mConfigBuilder!!.animDuration = this.mAnimDuration
			mConfigBuilder!!.isTouchToSeek = this.isTouchToSeek
			mConfigBuilder!!.isSeekStepSection = this.isSeekStepSection
			mConfigBuilder!!.isSeekBySection = this.isSeekBySection
			mConfigBuilder!!.bubbleColor = this.mBubbleColor
			mConfigBuilder!!.bubbleTextSize = this.mBubbleTextSize
			mConfigBuilder!!.bubbleTextColor = this.mBubbleTextColor
			mConfigBuilder!!.isAlwaysShowBubble = this.isAlwaysShowBubble
			mConfigBuilder!!.alwaysShowBubbleDelay = this.mAlwaysShowBubbleDelay
			mConfigBuilder!!.isHideBubble = this.isHideBubble
			mConfigBuilder!!.isRTL = this.isRtl
			return mConfigBuilder!!
		}

	override fun onSaveInstanceState(): Parcelable = Bundle().apply {
		putParcelable("save_instance", super.onSaveInstanceState())
		putFloat("progress", mProgress)
	}

	override fun onRestoreInstanceState(state: Parcelable) {
		if (state is Bundle) {
			mProgress = state.getFloat("progress")
			super.onRestoreInstanceState(state.getParcelable("save_instance"))
			mBubbleView?.setProgressText(if (isShowProgressInFloat) progressFloat.toString() else progress.toString())
			setProgress(mProgress)
			return
		}
		super.onRestoreInstanceState(state)
	}

	/**
	 * Listen to progress onChanged, onActionUp, onFinally
	 */
	interface OnProgressChangedListener {
		fun onProgressChanged(
			bubbleSeekBar: BubbleSeekBar?,
			progress: Int,
			progressFloat: Float,
			fromUser: Boolean
		)

		fun getProgressOnActionUp(
			bubbleSeekBar: BubbleSeekBar?,
			progress: Int,
			progressFloat: Float
		)

		fun getProgressOnFinally(
			bubbleSeekBar: BubbleSeekBar?,
			progress: Int,
			progressFloat: Float,
			fromUser: Boolean
		)
	}

	/**
	 * Listener adapter
	 * <br></br>
	 * usage like [AnimatorListenerAdapter]
	 */
	abstract class OnProgressChangedListenerAdapter : OnProgressChangedListener {
		override fun onProgressChanged(
			bubbleSeekBar: BubbleSeekBar?,
			progress: Int,
			progressFloat: Float,
			fromUser: Boolean
		) {
		}

		override fun getProgressOnActionUp(
			bubbleSeekBar: BubbleSeekBar?,
			progress: Int,
			progressFloat: Float
		) {
		}

		override fun getProgressOnFinally(
			bubbleSeekBar: BubbleSeekBar?,
			progress: Int,
			progressFloat: Float,
			fromUser: Boolean
		) {
		}
	}

	/**
	 * Customize the section texts under the track according to your demands by
	 * call [.setCustomSectionTextArray].
	 */
	interface CustomSectionTextArray {
		/**
		 *
		 *
		 * Customization goes here.
		 *
		 * For example:
		 * <pre> public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
		 * array.clear();
		 *
		 * array.put(0, "worst");
		 * array.put(4, "bad");
		 * array.put(6, "ok");
		 * array.put(8, "good");
		 * array.put(9, "great");
		 * array.put(10, "excellent");
		 * }</String></String></pre>
		 *
		 * @param sectionCount The section count of the `BubbleSeekBar`.
		 * @param array        The section texts array which had been initialized already. Customize
		 * the section text by changing one element's value of the SparseArray.
		 * The index key ∈[0, sectionCount].
		 * @return The customized section texts array. Can not be `null`.
		 */
		fun onCustomize(sectionCount: Int, array: SparseArray<String?>): SparseArray<String?>
	}

	/***********************************************************************************************
	 * custom bubble view  ***********************************
	 */
	private inner class BubbleView @JvmOverloads internal constructor(
		context: Context?,
		attrs: AttributeSet? = null,
		defStyleAttr: Int = 0
	) : View(context, attrs, defStyleAttr) {
		private val mBubblePaint: Paint by lazy { Paint() }
		private val mBubblePath: Path
		private val mBubbleRectF: RectF
		private val mRect: Rect
		private var mProgressText = ""
		override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec)
			setMeasuredDimension(3 * mBubbleRadius, 3 * mBubbleRadius)
			mBubbleRectF[measuredWidth / 2f - mBubbleRadius, 0f, measuredWidth / 2f + mBubbleRadius] =
				(2 * mBubbleRadius).toFloat()
		}

		override fun onDraw(canvas: Canvas) {
			super.onDraw(canvas)
			mBubblePath.reset()
			val x0 = measuredWidth / 2f
			val y0 = measuredHeight - mBubbleRadius / 3f
			mBubblePath.moveTo(x0, y0)
			val x1 = (measuredWidth / 2f - sqrt(3.0) / 2f * mBubbleRadius).toFloat()
			val y1 = 3 / 2f * mBubbleRadius
			mBubblePath.quadTo(
				x1 - BubbleUtils.dp2px(2), y1 - BubbleUtils.dp2px(2),
				x1, y1
			)
			mBubblePath.arcTo(mBubbleRectF, 150f, 240f)
			val x2 = (measuredWidth / 2f + sqrt(3.0) / 2f * mBubbleRadius).toFloat()
			mBubblePath.quadTo(
				x2 + BubbleUtils.dp2px(2), y1 - BubbleUtils.dp2px(2),
				x0, y0
			)
			mBubblePath.close()
			mBubblePaint.color = mBubbleColor
			canvas.drawPath(mBubblePath, mBubblePaint)
			mBubblePaint.textSize = mBubbleTextSize.toFloat()
			mBubblePaint.color = mBubbleTextColor
			mBubblePaint.getTextBounds(mProgressText, 0, mProgressText.length, mRect)
			val fm = mBubblePaint.fontMetrics
			val baseline = mBubbleRadius + (fm.descent - fm.ascent) / 2f - fm.descent
			canvas.drawText(mProgressText, measuredWidth / 2f, baseline, mBubblePaint)
		}

		fun setProgressText(progressText: String?) {
			if (progressText != null && mProgressText != progressText) {
				mProgressText = progressText
				invalidate()
			}
		}

		init {
			mBubblePaint.isAntiAlias = true
			mBubblePaint.textAlign = Paint.Align.CENTER
			mBubblePath = Path()
			mBubbleRectF = RectF()
			mRect = Rect()
		}
	}

	companion object {
		const val NONE = -1
	}

	init {
		val a = context.obtainStyledAttributes(attrs, R.styleable.BubbleSeekBar, 0, 0)
		min = a.getFloat(R.styleable.BubbleSeekBar_bsb_min, 0.0f)
		max = a.getFloat(R.styleable.BubbleSeekBar_bsb_max, 100.0f)
		mProgress = a.getFloat(R.styleable.BubbleSeekBar_bsb_progress, min)
		isFloatType = a.getBoolean(R.styleable.BubbleSeekBar_bsb_is_float_type, false)
		mTrackSize =
			a.getDimensionPixelSize(R.styleable.BubbleSeekBar_bsb_track_size, BubbleUtils.dp2px(2))
		mSecondTrackSize = a.getDimensionPixelSize(
			R.styleable.BubbleSeekBar_bsb_second_track_size,
			mTrackSize + BubbleUtils.dp2px(2)
		)
		mThumbRadius = a.getDimensionPixelSize(
			R.styleable.BubbleSeekBar_bsb_thumb_radius,
			mSecondTrackSize + BubbleUtils.dp2px(2)
		)
		mThumbRadiusOnDragging = a.getDimensionPixelSize(
			R.styleable.BubbleSeekBar_bsb_thumb_radius_on_dragging,
			mSecondTrackSize * 2
		)
		mSectionCount = a.getInteger(R.styleable.BubbleSeekBar_bsb_section_count, 10)
		mTrackColor = a.getColor(
			R.styleable.BubbleSeekBar_bsb_track_color,
			ContextCompat.getColor(context, R.color.colorPrimary)
		)
		mSecondTrackColor = a.getColor(
			R.styleable.BubbleSeekBar_bsb_second_track_color,
			ContextCompat.getColor(context, R.color.colorAccent)
		)
		mThumbColor = a.getColor(R.styleable.BubbleSeekBar_bsb_thumb_color, mSecondTrackColor)
		isShowSectionText = a.getBoolean(R.styleable.BubbleSeekBar_bsb_show_section_text, false)
		mSectionTextSize = a.getDimensionPixelSize(
			R.styleable.BubbleSeekBar_bsb_section_text_size,
			BubbleUtils.sp2px(14)
		)
		mSectionTextColor =
			a.getColor(R.styleable.BubbleSeekBar_bsb_section_text_color, mTrackColor)
		isSeekStepSection = a.getBoolean(R.styleable.BubbleSeekBar_bsb_seek_step_section, false)
		isSeekBySection = a.getBoolean(R.styleable.BubbleSeekBar_bsb_seek_by_section, false)
		val pos = a.getInteger(R.styleable.BubbleSeekBar_bsb_section_text_position, NONE)
		mSectionTextPosition = when (pos) {
			0 -> TextPosition.SIDES
			1 -> TextPosition.BOTTOM_SIDES
			2 -> TextPosition.BELOW_SECTION_MARK
			else -> NONE
		}
		mSectionTextInterval = a.getInteger(R.styleable.BubbleSeekBar_bsb_section_text_interval, 1)
		isShowThumbText = a.getBoolean(R.styleable.BubbleSeekBar_bsb_show_thumb_text, false)
		mThumbTextSize = a.getDimensionPixelSize(
			R.styleable.BubbleSeekBar_bsb_thumb_text_size,
			BubbleUtils.sp2px(14)
		)
		mThumbTextColor =
			a.getColor(R.styleable.BubbleSeekBar_bsb_thumb_text_color, mSecondTrackColor)
		mBubbleColor = a.getColor(R.styleable.BubbleSeekBar_bsb_bubble_color, mSecondTrackColor)
		mBubbleTextSize = a.getDimensionPixelSize(
			R.styleable.BubbleSeekBar_bsb_bubble_text_size,
			BubbleUtils.sp2px(14)
		)
		mBubbleTextColor = a.getColor(R.styleable.BubbleSeekBar_bsb_bubble_text_color, Color.WHITE)
		isShowSectionMark = a.getBoolean(R.styleable.BubbleSeekBar_bsb_show_section_mark, false)
		isAutoAdjustSectionMark =
			a.getBoolean(R.styleable.BubbleSeekBar_bsb_auto_adjust_section_mark, false)
		isShowProgressInFloat =
			a.getBoolean(R.styleable.BubbleSeekBar_bsb_show_progress_in_float, false)
		var duration = a.getInteger(R.styleable.BubbleSeekBar_bsb_anim_duration, -1)
		mAnimDuration = if (duration < 0) 200 else duration.toLong()
		isTouchToSeek = a.getBoolean(R.styleable.BubbleSeekBar_bsb_touch_to_seek, false)
		isAlwaysShowBubble = a.getBoolean(R.styleable.BubbleSeekBar_bsb_always_show_bubble, false)
		duration = a.getInteger(R.styleable.BubbleSeekBar_bsb_always_show_bubble_delay, 0)
		mAlwaysShowBubbleDelay = if (duration < 0) 0 else duration.toLong()
		isHideBubble = a.getBoolean(R.styleable.BubbleSeekBar_bsb_hide_bubble, false)
		isRtl = a.getBoolean(R.styleable.BubbleSeekBar_bsb_rtl, false)
		isEnabled = a.getBoolean(R.styleable.BubbleSeekBar_android_enabled, isEnabled)
		a.recycle()
		mPaint = Paint()
		mPaint.isAntiAlias = true
		mPaint.strokeCap = Paint.Cap.ROUND
		mPaint.textAlign = Paint.Align.CENTER
		mRectText = Rect()
		mTextSpace = BubbleUtils.dp2px(2)
		initConfigByPriority()
		if (!isHideBubble) {
			mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

			// init BubbleView
			mBubbleView = BubbleView(context)
			mBubbleView.setProgressText(if (isShowProgressInFloat) progressFloat.toString() else progress.toString())
			mLayoutParams = WindowManager.LayoutParams()
			mLayoutParams.gravity = Gravity.START or Gravity.TOP
			mLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
			mLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
			mLayoutParams.format = PixelFormat.TRANSLUCENT
			mLayoutParams.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
					or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
					//or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
					)
			// MIUI禁止了开发者使用TYPE_TOAST，Android 7.1.1 对TYPE_TOAST的使用更严格
			mLayoutParams.type =
				if (BubbleUtils.isMIUI || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
					WindowManager.LayoutParams.TYPE_APPLICATION
				} else {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
						WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
					} else {
						@Suppress("DEPRECATION")
						WindowManager.LayoutParams.TYPE_TOAST
					}
				}
			calculateRadiusOfBubble()
		} else {
			mWindowManager = null
			mBubbleView = null
			mLayoutParams = null
		}
	}
}