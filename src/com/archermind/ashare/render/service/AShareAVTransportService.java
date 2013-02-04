
package com.archermind.ashare.render.service;

import java.util.Locale;

import org.teleal.cling.binding.annotations.UpnpAction;
import org.teleal.cling.binding.annotations.UpnpInputArgument;
import org.teleal.cling.binding.annotations.UpnpOutputArgument;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.avtransport.AVTransportException;
import org.teleal.cling.support.avtransport.AbstractAVTransportService;
import org.teleal.cling.support.model.DeviceCapabilities;
import org.teleal.cling.support.model.MediaInfo;
import org.teleal.cling.support.model.PositionInfo;
import org.teleal.cling.support.model.SeekMode;
import org.teleal.cling.support.model.TransportInfo;
import org.teleal.cling.support.model.TransportSettings;
import org.teleal.cling.support.model.TransportState;
import org.teleal.cling.support.model.TransportStatus;

import android.os.RemoteException;

public class AShareAVTransportService extends AbstractAVTransportService {
    private final static boolean DEBUG = true;
    private final static int TIME_SECOND = 1000;
    private final static int TIME_MINUTE = TIME_SECOND * 60;
    private final static int TIME_HOUR = TIME_MINUTE * 60;

    private String mCurrentURI;

    private PlayServiceGetter mGetter;

    public AShareAVTransportService(PlayServiceGetter getter) {
        mGetter = getter;
    }

    @Override
    @UpnpAction(out = @UpnpOutputArgument(name = "Actions"))
    public String getCurrentTransportActions(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0)
            throws AVTransportException {
        LogUtil.logv(this, "getCurrentTransportActions", DEBUG);
        return null;
    }

    @Override
    @UpnpAction(out = {
            @UpnpOutputArgument(name = "PlayMedia", stateVariable = "PossiblePlaybackStorageMedia", getterName = "getPlayMediaString"),
            @UpnpOutputArgument(name = "RecMedia", stateVariable = "PossibleRecordStorageMedia", getterName = "getRecMediaString"),
            @UpnpOutputArgument(name = "RecQualityModes", stateVariable = "PossibleRecordQualityModes", getterName = "getRecQualityModesString")
    })
    public DeviceCapabilities getDeviceCapabilities(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0)
            throws AVTransportException {
        LogUtil.logv(this, "getDeviceCapabilities", DEBUG);
        return null;
    }

    @Override
    @UpnpAction(out = {
            @UpnpOutputArgument(name = "NrTracks", stateVariable = "NumberOfTracks", getterName = "getNumberOfTracks"),
            @UpnpOutputArgument(name = "MediaDuration", stateVariable = "CurrentMediaDuration", getterName = "getMediaDuration"),
            @UpnpOutputArgument(name = "CurrentURI", stateVariable = "AVTransportURI", getterName = "getCurrentURI"),
            @UpnpOutputArgument(name = "CurrentURIMetaData", stateVariable = "AVTransportURIMetaData", getterName = "getCurrentURIMetaData"),
            @UpnpOutputArgument(name = "NextURI", stateVariable = "NextAVTransportURI", getterName = "getNextURI"),
            @UpnpOutputArgument(name = "NextURIMetaData", stateVariable = "NextAVTransportURIMetaData", getterName = "getNextURIMetaData"),
            @UpnpOutputArgument(name = "PlayMedium", stateVariable = "PlaybackStorageMedium", getterName = "getPlayMedium"),
            @UpnpOutputArgument(name = "RecordMedium", stateVariable = "RecordStorageMedium", getterName = "getRecordMedium"),
            @UpnpOutputArgument(name = "WriteStatus", stateVariable = "RecordMediumWriteStatus", getterName = "getWriteStatus")
    })
    public MediaInfo getMediaInfo(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0)
            throws AVTransportException {
        LogUtil.logv(this, "getMediaInfo", DEBUG);
        return null;
    }

    @Override
    @UpnpAction(out = {
            @UpnpOutputArgument(name = "Track", stateVariable = "CurrentTrack", getterName = "getTrack"),
            @UpnpOutputArgument(name = "TrackDuration", stateVariable = "CurrentTrackDuration", getterName = "getTrackDuration"),
            @UpnpOutputArgument(name = "TrackMetaData", stateVariable = "CurrentTrackMetaData", getterName = "getTrackMetaData"),
            @UpnpOutputArgument(name = "TrackURI", stateVariable = "CurrentTrackURI", getterName = "getTrackURI"),
            @UpnpOutputArgument(name = "RelTime", stateVariable = "RelativeTimePosition", getterName = "getRelTime"),
            @UpnpOutputArgument(name = "AbsTime", stateVariable = "AbsoluteTimePosition", getterName = "getAbsTime"),
            @UpnpOutputArgument(name = "RelCount", stateVariable = "RelativeCounterPosition", getterName = "getRelCount"),
            @UpnpOutputArgument(name = "AbsCount", stateVariable = "AbsoluteCounterPosition", getterName = "getAbsCount")
    })
    public PositionInfo getPositionInfo(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0)
            throws AVTransportException {
        LogUtil.logv(this, "getPositionInfo", DEBUG);
        if (mGetter.getPlayService() != null) {
            int trackDuration = 0;
            try {
                trackDuration = mGetter.getPlayService().IGetDuration();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (trackDuration < 0) {
                trackDuration = 0;
            }
            int currentPos = 0;
            try {
                currentPos = mGetter.getPlayService().IGetCurrentPosition();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (currentPos < 0) {
                currentPos = 0;
            }
            PositionInfo curPosInfo = new PositionInfo(
                    0, formatTimeInfo(trackDuration), mCurrentURI,
                    formatTimeInfo(currentPos), formatTimeInfo(currentPos));
            return curPosInfo;
        }
        return new PositionInfo();
    }

    @Override
    @UpnpAction(out = {
            @UpnpOutputArgument(name = "CurrentTransportState", stateVariable = "TransportState", getterName = "getCurrentTransportState"),
            @UpnpOutputArgument(name = "CurrentTransportStatus", stateVariable = "TransportStatus", getterName = "getCurrentTransportStatus"),
            @UpnpOutputArgument(name = "CurrentSpeed", stateVariable = "TransportPlaySpeed", getterName = "getCurrentSpeed")
    })
    public TransportInfo getTransportInfo(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0)
            throws AVTransportException {
        LogUtil.logv(this, "getTransportInfo", DEBUG);
        if (mGetter.getPlayService() != null) {
            TransportState state = TransportState.STOPPED;
            try {
                state = TransportState.valueOrCustomOf(mGetter.getPlayService().IGetPlayerState());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            LogUtil.logv(this, "TransportStatus->"+state, DEBUG);
            return new TransportInfo(state,TransportStatus.OK);
        }
        return null;
    }

    @Override
    @UpnpAction(out = {
            @UpnpOutputArgument(name = "PlayMode", stateVariable = "CurrentPlayMode", getterName = "getPlayMode"),
            @UpnpOutputArgument(name = "RecQualityMode", stateVariable = "CurrentRecordQualityMode", getterName = "getRecQualityMode")
    })
    public TransportSettings getTransportSettings(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0)
            throws AVTransportException {
        LogUtil.logv(this, "getTransportSettings", DEBUG);
        return null;
    }

    @Override
    @UpnpAction
    public void next(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0)
            throws AVTransportException {
        LogUtil.logv(this, "next", DEBUG);

    }

    @Override
    @UpnpAction
    public void pause(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0)
            throws AVTransportException {
        LogUtil.logv(this, "pause", DEBUG);
        if (mGetter.getPlayService() != null) {
            try {
                mGetter.getPlayService().IPause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @UpnpAction
    public void play(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0,
            @UpnpInputArgument(name = "Speed", stateVariable = "TransportPlaySpeed")
            String arg1)
            throws AVTransportException {
        LogUtil.logv(this, "play", DEBUG);
        if (mGetter.getPlayService() != null) {
            try {
                mGetter.getPlayService().IPlay();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @UpnpAction
    public void previous(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0)
            throws AVTransportException {
        LogUtil.logv(this, "previous", DEBUG);
    }

    @Override
    @UpnpAction
    public void record(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0)
            throws AVTransportException {
        LogUtil.logv(this, "record", DEBUG);
    }

    @Override
    @UpnpAction
    public void seek(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0,
            @UpnpInputArgument(name = "Unit", stateVariable = "A_ARG_TYPE_SeekMode")
            String arg1,
            @UpnpInputArgument(name = "Target", stateVariable = "A_ARG_TYPE_SeekTarget")
            String arg2)
            throws AVTransportException {
        LogUtil.logv(this, "seek", DEBUG);
        if (mGetter.getPlayService() != null) {
            try {
                SeekMode seekMode;
                seekMode = SeekMode.valueOrExceptionOf(arg1);
                if (!seekMode.equals(SeekMode.REL_TIME)) {
                    throw new IllegalArgumentException();
                }
                int seconds = (int) ModelUtil.fromTimeString(arg2);
                mGetter.getPlayService().ISeek(seconds * TIME_SECOND);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @UpnpAction
    public void setAVTransportURI(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0,
            @UpnpInputArgument(name = "CurrentURI", stateVariable = "AVTransportURI")
            String arg1,
            @UpnpInputArgument(name = "CurrentURIMetaData", stateVariable = "AVTransportURIMetaData")
            String arg2)
            throws AVTransportException {
        LogUtil.logv(this, "setAVTransportURI", DEBUG);
        mCurrentURI = arg1;
        if (mGetter.getPlayService() != null) {
            try {
                mGetter.getPlayService().ISetUrl(arg1, arg2);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @UpnpAction
    public void setNextAVTransportURI(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0,
            @UpnpInputArgument(name = "NextURI", stateVariable = "AVTransportURI")
            String arg1,
            @UpnpInputArgument(name = "NextURIMetaData", stateVariable = "AVTransportURIMetaData")
            String arg2)
            throws AVTransportException {
        LogUtil.logv(this, "setNextAVTransportURI", DEBUG);
    }

    @Override
    @UpnpAction
    public void setPlayMode(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0,
            @UpnpInputArgument(name = "NewPlayMode", stateVariable = "CurrentPlayMode")
            String arg1)
            throws AVTransportException {
        LogUtil.logv(this, "setPlayMode", DEBUG);
    }

    @Override
    @UpnpAction
    public void setRecordQualityMode(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0,
            @UpnpInputArgument(name = "NewRecordQualityMode", stateVariable = "CurrentRecordQualityMode")
            String arg1)
            throws AVTransportException {
        LogUtil.logv(this, "setRecordQualityMode", DEBUG);
        if (mGetter.getPlayService() != null) {
            try {
                mGetter.getPlayService().IStop();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @UpnpAction
    public void stop(
            @UpnpInputArgument(name = "InstanceID")
            UnsignedIntegerFourBytes arg0)
            throws AVTransportException {
        LogUtil.logv(this, "stop", DEBUG);
        if (mGetter.getPlayService() != null) {
            try {
                mGetter.getPlayService().IStop();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private String formatTimeInfo(int timeVal) {
        int hour = timeVal / TIME_HOUR;
        int minute = (timeVal - hour * TIME_HOUR) / TIME_MINUTE;
        int second = (timeVal - hour * TIME_HOUR - minute * TIME_MINUTE)
                / TIME_SECOND;
        return String.format(Locale.US, "%02d:%02d:%02d", hour, minute, second);
    }

}
