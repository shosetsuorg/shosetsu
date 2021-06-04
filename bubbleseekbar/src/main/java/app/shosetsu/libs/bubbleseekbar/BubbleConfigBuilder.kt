package app.shosetsu.libs.bubbleseekbar

import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import app.shosetsu.libs.bubbleseekbar.BubbleSeekBar.TextPosition

/**
 * config BubbleSeekBar's attributes
 *
 *
 * Created by woxingxiao on 2017-03-14.
 */
class BubbleConfigBuilder internal constructor(private val mBubbleSeekBar: BubbleSeekBar) {
	var min = 0f
	var max = 0f
	var progress = 0f
	var isFloatType = false
	var trackSize = 0
	var secondTrackSize = 0
	var thumbRadius = 0
	var thumbRadiusOnDragging = 0

	@ColorInt
	var trackColor = 0

	@ColorInt
	var secondTrackColor = 0

	@ColorInt
	var thumbColor = 0
	var sectionCount = 0
		@Suppress("RedundantSetter")
		set(@IntRange(from = 1) value) {
			field = value
		}
	var isShowSectionMark = false
	var isAutoAdjustSectionMark = false
	var isShowSectionText = false
	var sectionTextSize = 0

	@ColorInt
	var sectionTextColor = 0

	@TextPosition
	var sectionTextPosition = 0
	var sectionTextInterval = 0
		@Suppress("RedundantSetter")
		set(@IntRange(from = 1) value) {
			field = value
		}
	var isShowThumbText = false
	var thumbTextSize = 0

	@ColorInt
	var thumbTextColor = 0
	var isShowProgressInFloat = false
	var animDuration: Long = 0
	var isTouchToSeek = false
	var isSeekStepSection = false
	var isSeekBySection = false

	@ColorInt
	var bubbleColor = 0
	var bubbleTextSize = 0

	@ColorInt
	var bubbleTextColor = 0
	var isAlwaysShowBubble = false
	var alwaysShowBubbleDelay: Long = 0
	var isHideBubble = false
	var isRTL = false

	fun build() {
		mBubbleSeekBar.config(this)
	}

	fun min(min: Float): BubbleConfigBuilder {
		this.min = min
		progress = min
		return this
	}


	fun max(max: Float): BubbleConfigBuilder {
		this.max = max
		return this
	}


	fun progress(progress: Float): BubbleConfigBuilder {
		this.progress = progress
		return this
	}


	fun floatType(): BubbleConfigBuilder {
		isFloatType = true
		return this
	}

	fun trackSize(dp: Int): BubbleConfigBuilder {
		trackSize = BubbleUtils.dp2px(dp)
		return this
	}

	fun secondTrackSize(dp: Int): BubbleConfigBuilder {
		secondTrackSize = BubbleUtils.dp2px(dp)
		return this
	}

	fun thumbRadius(dp: Int): BubbleConfigBuilder {
		thumbRadius = BubbleUtils.dp2px(dp)
		return this
	}

	fun thumbRadiusOnDragging(dp: Int): BubbleConfigBuilder {
		thumbRadiusOnDragging = BubbleUtils.dp2px(dp)
		return this
	}

	fun trackColor(@ColorInt color: Int): BubbleConfigBuilder {
		trackColor = color
		sectionTextColor = color
		return this
	}

	fun secondTrackColor(@ColorInt color: Int): BubbleConfigBuilder {
		secondTrackColor = color
		thumbColor = color
		thumbTextColor = color
		bubbleColor = color
		return this
	}


	fun thumbColor(@ColorInt color: Int): BubbleConfigBuilder {
		thumbColor = color
		return this
	}


	fun sectionCount(@IntRange(from = 1) count: Int): BubbleConfigBuilder {
		sectionCount = count
		return this
	}

	fun showSectionMark(): BubbleConfigBuilder {
		isShowSectionMark = true
		return this
	}

	fun autoAdjustSectionMark(): BubbleConfigBuilder {
		isAutoAdjustSectionMark = true
		return this
	}

	fun showSectionText(): BubbleConfigBuilder {
		isShowSectionText = true
		return this
	}


	fun sectionTextSize(sp: Int): BubbleConfigBuilder {
		sectionTextSize = BubbleUtils.sp2px(sp)
		return this
	}


	fun sectionTextColor(@ColorInt color: Int): BubbleConfigBuilder {
		sectionTextColor = color
		return this
	}


	fun sectionTextPosition(@TextPosition position: Int): BubbleConfigBuilder {
		sectionTextPosition = position
		return this
	}

	fun sectionTextInterval(@IntRange(from = 1) interval: Int): BubbleConfigBuilder {
		sectionTextInterval = interval
		return this
	}

	fun showThumbText(): BubbleConfigBuilder {
		isShowThumbText = true
		return this
	}

	fun thumbTextSize(sp: Int): BubbleConfigBuilder {
		thumbTextSize = BubbleUtils.sp2px(sp)
		return this
	}


	fun thumbTextColor(@ColorInt color: Int): BubbleConfigBuilder {
		thumbTextColor = color
		return this
	}

	fun showProgressInFloat(): BubbleConfigBuilder {
		isShowProgressInFloat = true
		return this
	}


	fun animDuration(duration: Long): BubbleConfigBuilder {
		animDuration = duration
		return this
	}

	fun touchToSeek(): BubbleConfigBuilder {
		isTouchToSeek = true
		return this
	}

	fun seekStepSection(): BubbleConfigBuilder {
		isSeekStepSection = true
		return this
	}

	fun seekBySection(): BubbleConfigBuilder {
		isSeekBySection = true
		return this
	}


	fun bubbleColor(@ColorInt color: Int): BubbleConfigBuilder {
		bubbleColor = color
		return this
	}

	fun bubbleTextSize(sp: Int): BubbleConfigBuilder {
		bubbleTextSize = BubbleUtils.sp2px(sp)
		return this
	}


	fun bubbleTextColor(@ColorInt color: Int): BubbleConfigBuilder {
		bubbleTextColor = color
		return this
	}

	fun alwaysShowBubble(): BubbleConfigBuilder {
		isAlwaysShowBubble = true
		return this
	}


	fun alwaysShowBubbleDelay(delay: Long): BubbleConfigBuilder {
		alwaysShowBubbleDelay = delay
		return this
	}

	fun hideBubble(): BubbleConfigBuilder {
		isHideBubble = true
		return this
	}

	fun rtl(rtl: Boolean): BubbleConfigBuilder {
		isRTL = rtl
		return this
	}
}