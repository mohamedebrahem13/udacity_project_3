package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonText = ""
    private var buttonWidth = 0
    private var circleAngel = 0f

    //Button Custom Attrs
    private var buttonBackgroundColor = 0
    private var buttonTextColor = 0
    private var buttonInmationColor=0




    //Object ValueAnimator to animate Button and Circle
    private var circleAnimator = ValueAnimator()
    private var buttonAnimator = ValueAnimator()



    //Paint Object for our Button
    private val paintButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
        typeface = Typeface.create("", Typeface.BOLD)
    }

    //Paint Object for the circle
    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        style = Paint.Style.FILL
        color = context.getColor(R.color.colorAccent)
    }

    //Paint Object for Button Text
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = resources.getDimension(R.dimen.text_size_large)
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                // change button text when the Animation start
                buttonText = resources.getString(R.string.button_loading)
                circleAnimator = ValueAnimator.ofFloat(0f, 360f)
                    .apply {
                        duration = 3000
                        repeatCount = ValueAnimator.INFINITE
                        repeatMode = ValueAnimator.REVERSE
                        interpolator = DecelerateInterpolator(1f)
                        addUpdateListener { circleinimate ->
                            //  TO UPDATE THE CIRCLE ANGEL AND I USE IT IN drawanimaitedcircle() TO DRAW CIRCLE ANIMATION
                            circleAngel = animatedValue as Float
                            //CALL INVALIDATE TO RE DRAW THE CANVAS WITH CIRCLE ANIMATION
                            invalidate()

                        }
                    }
                buttonAnimator = ValueAnimator.ofInt(0, widthSize)
                    .apply {
                        duration = 3000
                        repeatMode = ValueAnimator.RESTART
                        repeatCount = 1
                        addUpdateListener {
                            //  TO UPDATE THE buttonWidth AND I USE IT IN drawainmatiedrectangel() TO DRAW BUTTON ANIMATION

                            buttonWidth = animatedValue as Int
                            //CALL INVALIDATE TO RE DRAW THE CANVAS WITH BUTTON ANIMATION

                            invalidate()
                        }
                    }
                // START THE ANIMATION
                circleAnimator.start()
                buttonAnimator.start()
            }

            ButtonState.Completed -> {
                // WHEN THE DOWNLOAD MANGER FINISHED STOP THE ANIMATION
                stopAnimation()

            }
        }
    }

    private fun stopAnimation() {
        // RESET THE BUTTON TEXT
        buttonText = resources.getString(R.string.download_text)
        // RESET THE BUTTON WIDTH BECAUSE IF WE NEED THE ANIMATION AGAIN WE START IT FROM BEGINNING
        buttonWidth = 0
        // STOP CIRCLE ANIMATION
        circleAnimator.end()
        // STOP BUTTON ANIMATION
        buttonAnimator.end()
    }





    init {
        //Clickable property that enables the View to accept User Input
        // AND I USED is clickable instead of on click listener to LET THE USER TO USE ONCLICK LINTER IF HE WANT
        isClickable = true
        //GET BUTTON TEXT FROM STRING RESOURCE
        buttonText = resources.getText(R.string.download_text).toString()
        // GET CUSTOM ATTRS FROM ATTRS.XML AND WE HAVE 3 CUSTOM ATTRS
     val a=  context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0)
        //  COLOR TO CHANGE THE BACKGROUND OF THE  BUTTON AND WE HAVE DEFAULT VALUE IF I DON'T USE IT IN XML
        buttonBackgroundColor=a.getColor(R.styleable.LoadingButton_buttonBack,Color.RED)
        //SAME HERE TEXT COLOR WITH DEFAULT VALUE
        buttonTextColor=a.getColor(R.styleable.LoadingButton_textCor, Color.GREEN)
        //SAME HERE BUT FOR THE BUTTON ANIMATION COLOR
        buttonInmationColor=a.getColor(R.styleable.LoadingButton_inimationcor,Color.GRAY)

           // DEFAULT VALUE FOR THE STATE
        buttonState = ButtonState.Clicked

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // DRAW THE BUTTON FIRST TIME AND WE DON'T START THE ANIMATION yet
        drawmainrectangel(canvas)

        if(buttonState == ButtonState.Loading){
            // here the download manger is working and we need to start the animations
            drawainmatiedrectangel(canvas)
            drawanimaitedcircle(canvas)
        }
        // we draw the text after the animation is finished and if we don't have the animation yet so we call it last one
        drawbuttontext(canvas)

    }




    private fun drawmainrectangel(canvas: Canvas?){
        // here we use the buttoncolor from custom attrs
        paintButton.color = buttonBackgroundColor
        // draw rect
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paintButton)
    }
    private fun drawainmatiedrectangel(canvas: Canvas?){
        // here we use the button animated color from custom attrs
        paintButton.color =buttonInmationColor
        // draw animated rect by using the value from buttonWidth that we updated using ValueAnimator
        canvas!!.drawRect(0f, 0f, widthSize.toFloat() * buttonWidth / 100, heightSize.toFloat(), paintButton)
    }
    private fun drawbuttontext(canvas: Canvas?){
        // CENTER THE TEXT IN THE MID OF THE BUTTON
        paintText.textAlign = Paint.Align.CENTER

        // here we use the button TEXT COLOR from custom attrs
        paintText.color=buttonTextColor
        // DRAW THE TEXT AND USING THE paintText
        canvas?.drawText(
            buttonText,
            widthSize.toFloat() / 2,
            heightSize / 1.7f,
            paintText)
    }
    private fun drawanimaitedcircle(canvas: Canvas?){
        // draw animated circle by using the value from circleAngel that we updated using ValueAnimator

        canvas?.drawArc(
            widthSize - 140f, heightSize / 2 - 40f,
            widthSize - 75f, heightSize / 2 + 40f,
            0f, circleAngel, true, paintCircle)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}