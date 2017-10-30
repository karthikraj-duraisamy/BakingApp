package net.karthikraj.excercise.bakingapp.recipiedetail;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import net.karthikraj.excercise.bakingapp.R;
import net.karthikraj.excercise.bakingapp.model.Step;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karthik on 29/10/17.
 */

public class StepsVideoFragment extends Fragment implements ExoPlayer.EventListener, ImageView.OnClickListener{

    private static final String TAG = StepsVideoFragment.class.getSimpleName();

    private static final String RESUME_POSITION = "resume_position";

    @BindView(R.id.exoplayer)
    SimpleExoPlayerView stepPlayerView;
    @BindView(R.id.video_buffering)
    ProgressBar video_buffering;
    @BindView(R.id.refresh_button)
    ImageView refresh_btn;
    @BindView(R.id.play_button) ImageView play_btn;
    @BindView(R.id.video_thumbnail) ImageView video_thumbnail;
    @BindView(R.id.no_video) ImageView no_video_icon;
    @Nullable @BindView(R.id.btn_next)
    Button buttonNext;
    @Nullable @BindView(R.id.btn_previous) Button buttonPrev;
    @Nullable @BindView(R.id.descrition_textview)
    TextView descrition_textview;

    private static final String STEP_PARCELABLE = "step_parcelable";
    private static final String NUMBER_OF_STEPS = "number_of_steps";

    private SimpleExoPlayer stepPlayer;
    private Handler mainHandler;
    private Context context;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private String userAgent;
    private int num_steps;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    private Step step;

    private boolean playWhenReady;
    private int currentWindow;
    private long playbackPosition;

    StepChangeListener stepChangeListener;

    public interface StepChangeListener{
        void onNextClick(int id);
        void onPrevClick(int id);
    }


    public StepsVideoFragment() {
    }

    public static StepsVideoFragment newInstance(Step step, int num_steps){
        StepsVideoFragment fragment = new StepsVideoFragment();
        Bundle args = new Bundle();
        args.putParcelable(STEP_PARCELABLE,step);
        args.putInt(NUMBER_OF_STEPS,num_steps);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        if(getArguments() != null){
            step = getArguments().getParcelable(STEP_PARCELABLE);
            num_steps = getArguments().getInt(NUMBER_OF_STEPS);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(stepPlayer != null) {
            outState.putLong(RESUME_POSITION, playbackPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_steps_video, container, false);
        ButterKnife.bind(this,rootView);
        setRetainInstance(true);
        checkOrientation();
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            loadData();
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || stepPlayer == null)) {
            loadData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }



    private void loadData(){
        userAgent = Util.getUserAgent(context,"BakingApp");
        refresh_btn.setOnClickListener(this);
        playWhenReady = true;
        mediaDataSourceFactory = new DefaultDataSourceFactory(context,BANDWIDTH_METER,
                new DefaultHttpDataSourceFactory(userAgent,BANDWIDTH_METER));
        mainHandler = new Handler();
        stepPlayerView.requestFocus();

        setPrevNextButtons();
        if(descrition_textview != null){
            descrition_textview.setText(step.getDescription());
        }
        startPlayer();

    }

    private void checkOrientation(){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ){
            try {
                ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
            } catch (NullPointerException e) {
                Log.w(TAG, "Toolbar is not attached with the activity");
            }
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //Remove notification bar
        }
    }

    private void setPrevNextButtons(){

        if(buttonNext == null || buttonPrev == null ) return;
        if(step.getId() != num_steps-1) {
            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stepChangeListener.onNextClick(step.getId());
                }
            });
        }else{
            buttonNext.setVisibility(View.INVISIBLE);
        }

        if(step.getId() > 0){
            buttonPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stepChangeListener.onPrevClick(step.getId());
                }
            });
        }else{
            buttonPrev.setVisibility(View.INVISIBLE);
        }
    }


    private void startPlayer(){

        if (!step.getThumbnailURL().isEmpty()) {
            Picasso.Builder builder = new Picasso.Builder(getContext());
            builder.listener(new Picasso.Listener()
            {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
                {
                    initializePlayer();
                    play_btn.setVisibility(View.GONE);
                }

            });
            builder.build().load(step.getThumbnailURL()).into(video_thumbnail);
            play_btn.setVisibility(View.VISIBLE);
            play_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play_btn.setVisibility(View.GONE);
                    initializePlayer();
                }
            });
        }else {
            video_thumbnail.setVisibility(View.GONE);
            initializePlayer();
        }
    }

    private void initializePlayer() {

        if(stepPlayer != null || step.getVideoURL().isEmpty()) {
            no_video_icon.setVisibility(View.VISIBLE);
            return;
        } else {
            no_video_icon.setVisibility(View.GONE);
        }

        stepPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getActivity()),
                new DefaultTrackSelector(), new DefaultLoadControl());

        stepPlayerView.setPlayer(stepPlayer);
        stepPlayer.addListener(this);
        stepPlayer.setPlayWhenReady(playWhenReady);
        stepPlayer.seekTo(currentWindow, playbackPosition);

        Uri uri = Uri.parse(step.getVideoURL());
        MediaSource mediaSource = buildMediaSource(uri);
        stepPlayer.prepare(mediaSource, true, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }



    private void releasePlayer() {
        if (stepPlayer != null) {
            playbackPosition = stepPlayer.getCurrentPosition();
            currentWindow = stepPlayer.getCurrentWindowIndex();
            playWhenReady = stepPlayer.getPlayWhenReady();
            stepPlayer.release();
            stepPlayer = null;
        }
    }


    private void refreshVideo(){
        playbackPosition = 0;
        stepPlayer.seekTo(0);
        refresh_btn.setVisibility(View.GONE);
    }

    //Refresh Button clickListener
    @Override
    public void onClick(View v) {
        if(stepPlayer != null){
            refreshVideo();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            stepChangeListener = (StepChangeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement onChangeStepListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        stepChangeListener = null;
    }

    //----------ExoPlayer EventListeners----------
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_BUFFERING) {
            video_buffering.setVisibility(View.VISIBLE);
        }else if(playbackState == ExoPlayer.STATE_ENDED) {
            refresh_btn.setVisibility(View.VISIBLE);
        }else{
            video_buffering.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        String errorString = null;
        if (error.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = error.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = getString(R.string.error_no_secure_decoder,
                                decoderInitializationException.mimeType);
                    } else {
                        errorString = getString(R.string.error_no_decoder,
                                decoderInitializationException.mimeType);
                    }
                } else {
                    errorString = getString(R.string.error_instantiating_decoder,
                            decoderInitializationException.decoderName);
                }
            }
        }
        if (errorString != null) {
            Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

}
