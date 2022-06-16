package mooze.darzi.custom_video_player_exo;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout videoLayout ;
    private static ImageView frward_img,backward_img,playbtn,pausebtn,fullscreen,fullscreenexit,showimgUp,showimgDown;
    private RelativeLayout mediacontrols;
    private int check;
    private TextView startTime,endTime;
    private static TextView rewindtxt,frwrdtxt;
    private SeekBar seekBar;
    private Handler videoHandler;
    private Runnable videoRunnable;
    private CountDownTimer cTimer = null;
    private ProgressBar progressBar,bufferbar;
    private YoutubeLayout youtubeLayout;
    private boolean isMaximise=true;
    private boolean isPlaying=false;

    private String url="https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4";


    private SimpleExoPlayerView exoplayerview;
    private SimpleExoPlayer exoPlayer;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        videoLayout =findViewById(R.id.video_layout);
        frward_img=findViewById(R.id.frward_img);
        backward_img=findViewById(R.id.backward_img);
        FrameLayout bbkframe = findViewById(R.id.bbkframe);
        FrameLayout ffrdframe = findViewById(R.id.ffrdframe);
        mediacontrols=findViewById(R.id.mediacontrols);
        playbtn=findViewById(R.id.playbtn);
        pausebtn=findViewById(R.id.pausebtn);
        startTime=findViewById(R.id.starttime);
        endTime=findViewById(R.id.endtime);
        seekBar=findViewById(R.id.seekbar);
        rewindtxt=findViewById(R.id.rewindtxt);
        frwrdtxt=findViewById(R.id.frwrdtxt);
        fullscreen=findViewById(R.id.fullscreen);
        fullscreenexit=findViewById(R.id.fullscreenexit);
        progressBar=findViewById(R.id.progress);
        bufferbar=findViewById(R.id.bufferbar);
        youtubeLayout = findViewById(R.id.dragLayout);
        showimgDown=findViewById(R.id.showdown);
        showimgUp=findViewById(R.id.showup);
        exoplayerview=findViewById(R.id.exoplayerview);


        setHandler();

        initiateVideoExo();

        final GestureDetector gd = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (check==1)
                    fastRewind();
                else if (check==0)
                    fastForward();

                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                startTimer();
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {

                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
                if (isMaximise) {
                    minimiseView();
                }
                else {
                    maximiseView();
                }
                return true;
            }
        });

        ffrdframe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                check=1;
                return  gd.onTouchEvent(event);
            }
        });

        bbkframe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                check=0;
                return gd.onTouchEvent(event);
            }
        });

        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(500);
                AnimationSet animation = new AnimationSet(false);
                animation.addAnimation(fadeIn);
                pausebtn.setAnimation(animation);

                exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
                pausebtn.setVisibility(View.VISIBLE);
                playbtn.setVisibility(View.GONE);

            }
        });

        pausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(500);
                AnimationSet animation = new AnimationSet(false);
                animation.addAnimation(fadeIn);
                playbtn.setAnimation(animation);


                pausebtn.setVisibility(View.GONE);
                playbtn.setVisibility(View.VISIBLE);

                exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
            }
        });

        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                int TIME_OUT = 2500;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setRequestedOrientation(SCREEN_ORIENTATION_SENSOR);
                    }
                }, TIME_OUT);
            }
        });

        fullscreenexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                int TIME_OUT = 2500;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setRequestedOrientation(SCREEN_ORIENTATION_SENSOR);
                    }
                },TIME_OUT);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    exoPlayer.seekTo(progress);
                    exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void initiateVideoExo()
    {
        try
        {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = ExoPlayerFactory.newSimpleInstance(MainActivity.this, trackSelector);

            Uri videouri = Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");

            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);

            exoplayerview.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);

            exoPlayer.addListener(new ExoPlayer.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                    if (playWhenReady && playbackState == exoPlayer.STATE_READY) {
                        isPlaying=true;

                        videoHandler = new Handler();
                        videoRunnable = new Runnable() {
                            @Override
                            public void run() {
                                bufferbar.setProgress(exoPlayer.getBufferedPercentage());
                                startTime.setText(convertIntToTime((int) exoPlayer.getCurrentPosition()));
                                endTime.setText(convertIntToTime((int) (exoPlayer.getDuration() - exoPlayer.getCurrentPosition())));
                                seekBar.setMax((int) exoPlayer.getDuration());
                                seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                                videoHandler.postDelayed(videoRunnable, 0);
                            }
                        };
                        videoHandler.postDelayed(videoRunnable, 0);
                    }
                    else if (playWhenReady) {
                        isPlaying=false;
                    }
                    else {
                        isPlaying=false;
                    }
                    if (playbackState == ExoPlayer.STATE_BUFFERING){
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }

                @Override
                public void onPositionDiscontinuity(int reason) {

                }


                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }

                @Override
                public void onSeekProcessed() {

                }
            });

        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, "" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 230.0f,getResources().getDisplayMetrics());

        ViewGroup.LayoutParams params = videoLayout.getLayoutParams();

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            hideSystemUI();
            params.width  = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            fullscreen.setVisibility(View.GONE);
            fullscreenexit.setVisibility(View.VISIBLE);
            videoLayout.requestLayout();
            showimgDown.setVisibility(View.GONE);
            showimgUp.setVisibility(View.GONE);

            if (!isMaximise)
            {
                exoPlayer.setPlayWhenReady(false);

            }

        }
        else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            params.width  = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = height;

            fullscreen.setVisibility(View.VISIBLE);
            fullscreenexit.setVisibility(View.GONE);
            videoLayout.requestLayout();
            showimgDown.setVisibility(View.VISIBLE);
            showimgUp.setVisibility(View.GONE);


        }

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Configuration chechConfig = getResources().getConfiguration();

        if(chechConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            if (hasFocus) {
                hideSystemUI();
            }
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public static void animForward(final ImageView view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimationForward(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        prepareAnimationForward(alphaAnimation);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(500);//u can adjust yourself
        view.startAnimation(animation);
    }

    private static Animation prepareAnimationForward(Animation animation){
        animation.setRepeatCount(1);
        animation.setRepeatMode(Animation.REVERSE);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new AccelerateInterpolator());
                fadeOut.setStartOffset(100);
                fadeOut.setDuration(100);
                AnimationSet animation1 = new AnimationSet(false);
                animation1.addAnimation(fadeOut);

                frwrdtxt.setAnimation(animation1);

                frward_img.setVisibility(View.GONE);
                frwrdtxt.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animation;
    }

    public static void animBackward(final ImageView view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        prepareAnimationBackward(scaleAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        prepareAnimationBackward(alphaAnimation);

        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(alphaAnimation);
        animation.addAnimation(scaleAnimation);
        animation.setDuration(500);//u can adjust yourself
        view.startAnimation(animation);
    }

    private static Animation prepareAnimationBackward(Animation animation){
        animation.setRepeatCount(1);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new AccelerateInterpolator());
                fadeOut.setStartOffset(100);
                fadeOut.setDuration(100);
                AnimationSet animation1 = new AnimationSet(false);
                animation1.addAnimation(fadeOut);

                rewindtxt.setAnimation(animation1);

                backward_img.setVisibility(View.GONE);
                rewindtxt.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animation;
    }

    void fastForward()
    {
        exoPlayer.seekTo(exoPlayer.getCurrentPosition() + 10000);
        frward_img.setVisibility(View.VISIBLE);
        frwrdtxt.setVisibility(View.VISIBLE);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(100);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        frwrdtxt.setAnimation(animation);
        animForward(frward_img);
    }
    void fastRewind()
    {
        exoPlayer.seekTo(exoPlayer.getCurrentPosition() - 10000);
        backward_img.setVisibility(View.VISIBLE);
        rewindtxt.setVisibility(View.VISIBLE);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(100);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        rewindtxt.setAnimation(animation);
        animBackward(backward_img);
    }

    void startTimer() {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(500);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        mediacontrols.setAnimation(animation);

        cTimer = new CountDownTimer(1500, 1000) {
            public void onTick(long millisUntilFinished) {
                mediacontrols.setVisibility(View.VISIBLE);

                if (isPlaying) {
                    pausebtn.setVisibility(View.VISIBLE);
                    playbtn.setVisibility(View.GONE);
                }
                else {
                    playbtn.setVisibility(View.VISIBLE);
                    pausebtn.setVisibility(View.GONE);
                }
            }

            public void onFinish() {
                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new AccelerateInterpolator());
                fadeOut.setStartOffset(100);
                fadeOut.setDuration(100);
                AnimationSet animation = new AnimationSet(false);
                animation.addAnimation(fadeOut);
                mediacontrols.setAnimation(animation);

                mediacontrols.setVisibility(View.GONE);
                pausebtn.setVisibility(View.GONE);
                playbtn.setVisibility(View.GONE);

            }
        };
        cTimer.start();
    }

    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        cancelTimer();
        super.onDestroy();
    }

    void setHandler(){
        videoHandler=new Handler();
        videoRunnable=new Runnable() {
            @Override
            public void run() {
                if (exoPlayer.getDuration()>0)
                {
                    long currentPosition=exoPlayer.getCurrentPosition();
                    seekBar.setProgress((int) currentPosition);
                    startTime.setText(""+convertIntToTime((int) currentPosition));
                    endTime.setText(""+convertIntToTime((int) (exoPlayer.getDuration() - currentPosition)));
                }

                videoHandler.postDelayed(this,0);
            }
        };

        videoHandler.postDelayed(videoRunnable,500);
    }

    private String convertIntToTime(int ms) {
        String time = null;
        int x,seconds,minutes,hours;
        x= ms/1000;
        seconds= x % 60;
        x /= 60;
        minutes= x % 60;
        x /= 60;
        hours= x % 24;

        if (hours != 0)
            time=String.format("%02d",hours) + ":" + String.format("%02d",minutes)+ ":" +String.format("%02d",seconds);
        else
            time=String.format("%02d",minutes)+ ":" +String.format("%02d",seconds);

        return time;
    }

    @Override
    protected void onPause() {
        if (exoPlayer != null)
            exoPlayer.setPlayWhenReady(false);
        super.onPause();
    }
    @Override
    public void onBackPressed() {
        Configuration chechConfig = getResources().getConfiguration();

        if(chechConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else if (chechConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            super.onBackPressed();
        }
    }

    public void dissmissControls(View view) {
        cancelTimer();
        if (mediacontrols.getVisibility()==View.VISIBLE)
        {
            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setStartOffset(100);
            fadeOut.setDuration(100);
            AnimationSet animation = new AnimationSet(false);
            animation.addAnimation(fadeOut);

            mediacontrols.setAnimation(animation);

            mediacontrols.setVisibility(View.GONE);
            pausebtn.setVisibility(View.GONE);
            playbtn.setVisibility(View.GONE);
        }
        else
        {
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(500);
            AnimationSet animation = new AnimationSet(false);
            animation.addAnimation(fadeIn);
            mediacontrols.setAnimation(animation);

            mediacontrols.setVisibility(View.VISIBLE);

            if (isPlaying) {
                pausebtn.setVisibility(View.VISIBLE);
                playbtn.setVisibility(View.GONE);
            }
            else {
                playbtn.setVisibility(View.VISIBLE);
                pausebtn.setVisibility(View.GONE);
            }
        }
    }

    public void showDown(View view) {
        minimiseView();
    }

    public void showUp(View view) {
        maximiseView();
    }

    void minimiseView()
    {
        showimgUp.setVisibility(View.VISIBLE);
        showimgDown.setVisibility(View.GONE);
        youtubeLayout.minimize();
        fullscreen.setVisibility(View.GONE);
        youtubeLayout.minimize();
        isMaximise=false;
    }

    void maximiseView()
    {
        showimgUp.setVisibility(View.GONE);
        showimgDown.setVisibility(View.VISIBLE);
        youtubeLayout.maximize();
        fullscreen.setVisibility(View.VISIBLE);
        youtubeLayout.maximize();
        isMaximise=true;
    }

    public void samplevideo(View view) {
        url="http://139.59.31.217:4864/api/videos/720p/food-showreel.mp4";
        //initiateVideo();
    }

    public void samplevideo2(View view) {
        url="http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4";
        initiateVideoExo();
    }
}