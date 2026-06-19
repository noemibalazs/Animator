package com.noemi_balazs.animator.common

import androidx.compose.runtime.Composable
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UILabel
import platform.UIKit.UIScreen
import platform.UIKit.UIView
import platform.UIKit.UIViewAnimationOptionCurveEaseIn
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

class IosToastManager : ToastManager {

    override fun showMessage(message: String, isShortDuration: Boolean) {
        val visibleDuration = if (isShortDuration) 2.0 else 3.5

        dispatch_async(dispatch_get_main_queue()) {
            val window = UIApplication.sharedApplication.keyWindow ?: return@dispatch_async
            val screenBounds = UIScreen.mainScreen.bounds
            val screenWidth = screenBounds.useContents { size.width }
            val screenHeight = screenBounds.useContents { size.height }
            val safeAreaBottom = window.safeAreaInsets.useContents { bottom }

            val horizontalPadding = 32.0
            val verticalPadding = 20.0
            val maxTextWidth = screenWidth - 80.0 - horizontalPadding

            val measureLabel = UILabel().apply {
                text = message
                font = UIFont.systemFontOfSize(14.0)
                numberOfLines = 2
            }
            val textSize = measureLabel.sizeThatFits(CGSizeMake(maxTextWidth, 100.0))
            val textWidth = textSize.useContents { width }
            val textHeight = textSize.useContents { height }

            val toastWidth = textWidth + horizontalPadding
            val toastHeight = textHeight + verticalPadding
            val toastX = (screenWidth - toastWidth) / 2.0
            val toastY = screenHeight - safeAreaBottom - 70.0 - toastHeight

            val toastView =
                UIView(frame = CGRectMake(toastX, toastY, toastWidth, toastHeight)).apply {
                    backgroundColor = UIColor(
                        red = 50.0 / 255.0,
                        green = 50.0 / 255.0,
                        blue = 50.0 / 255.0,
                        alpha = 0.95,
                    )
                    layer.cornerRadius = toastHeight / 2.0
                    layer.masksToBounds = true
                    userInteractionEnabled = false
                    alpha = 0.0
                }

            val label = UILabel(
                frame = CGRectMake(
                    horizontalPadding / 2.0,
                    verticalPadding / 2.0,
                    textWidth,
                    textHeight,
                )
            ).apply {
                text = message
                textColor = UIColor.whiteColor
                font = UIFont.systemFontOfSize(14.0)
                textAlignment = NSTextAlignmentCenter
                numberOfLines = 2
            }

            toastView.addSubview(label)
            window.addSubview(toastView)

            UIView.animateWithDuration(0.2) {
                toastView.alpha = 1.0
            }

            UIView.animateWithDuration(
                duration = 0.3,
                delay = visibleDuration,
                options = UIViewAnimationOptionCurveEaseIn,
                animations = { toastView.alpha = 0.0 },
                completion = { toastView.removeFromSuperview() },
            )
        }
    }
}

@Composable
actual fun provideToastManager(): ToastManager = IosToastManager()

