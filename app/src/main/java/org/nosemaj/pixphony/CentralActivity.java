package org.nosemaj.pixphony;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import jp.kshoji.blemidi.central.BleMidiCentralProvider;
import jp.kshoji.blemidi.device.MidiInputDevice;
import jp.kshoji.blemidi.device.MidiOutputDevice;
import jp.kshoji.blemidi.listener.OnMidiDeviceAttachedListener;
import jp.kshoji.blemidi.listener.OnMidiDeviceDetachedListener;
import jp.kshoji.blemidi.listener.OnMidiInputEventListener;
import jp.kshoji.blemidi.listener.OnMidiScanStatusListener;
import jp.kshoji.blemidi.util.BleUtils;

/**
 * Activity for BLE MIDI Central Application
 *
 * @author K.Shoji
 */
public class CentralActivity extends Activity {
    /*
     * 0 scans forever -- TODO: good for testing, but release?
     */
    private static final int BLE_SCAN_TIME_MS = 0;

    private static final int MIDI_DEVICE_DETACHED = 0;
    private static final int MIDI_DEVICE_ATTACHED = 1;

    private static final int WHITE_KEY_COLOR = 0xFFFFFFFF;
    private static final int BLACK_KEY_COLOR = 0xFF808080;

    BleMidiCentralProvider bleMidiCentralProvider;
    MenuItem toggleScanMenu;
    boolean isScanning = false;

    SoundPlayer soundPlayer;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.central, menu);
        toggleScanMenu = menu.getItem(0);

        if (isScanning) {
            toggleScanMenu.setTitle(R.string.stop_scan);
        } else {
            toggleScanMenu.setTitle(R.string.start_scan);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle_scan:
                if (isScanning) {
                    bleMidiCentralProvider.stopScanDevice();
                } else {
                    bleMidiCentralProvider.startScanDevice(BLE_SCAN_TIME_MS);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // User interface
    final Handler midiInputEventHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (midiInputEventAdapter != null) {
                midiInputEventAdapter.add((String)msg.obj);
            }
            // message handled successfully
            return true;
        }
    });

    final Handler midiConnectionChangedHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.obj instanceof MidiInputDevice) {
                MidiInputDevice midiInputDevice = (MidiInputDevice) msg.obj;
                if (msg.arg1 == MIDI_DEVICE_ATTACHED) {
                    connectedDevicesAdapter.remove(midiInputDevice);
                    connectedDevicesAdapter.add(midiInputDevice);
                    connectedDevicesAdapter.notifyDataSetChanged();
                } else {
                    connectedDevicesAdapter.remove(midiInputDevice);
                    connectedDevicesAdapter.notifyDataSetChanged();
                }
            }

            // message handled successfully
            return true;
        }
    });

    ArrayAdapter<String> midiInputEventAdapter;
    Spinner deviceSpinner;
    ArrayAdapter<MidiInputDevice> connectedDevicesAdapter;

    /**
     * Choose device from spinner
     *
     * @return chosen {@link jp.kshoji.blemidi.device.MidiInputDevice}
     */
    MidiInputDevice getBleMidiDeviceFromSpinner() {
        if (deviceSpinner != null && 
                deviceSpinner.getSelectedItemPosition() >= 0 &&
                connectedDevicesAdapter != null &&
                !connectedDevicesAdapter.isEmpty()) {

            MidiInputDevice device = connectedDevicesAdapter.getItem(deviceSpinner.getSelectedItemPosition());
            if (device != null) {
                Set<MidiInputDevice> midiInputDevices = bleMidiCentralProvider.getMidiInputDevices();

                if (midiInputDevices.size() > 0) {
                    // returns the first one.
                    return (MidiInputDevice) midiInputDevices.toArray()[0];
                }
            }
        }

        return null;
    }

    OnMidiInputEventListener onMidiInputEventListener = new OnMidiInputEventListener() {
        @Override
        public void onMidiSystemExclusive(@NonNull MidiInputDevice sender, @NonNull byte[] systemExclusive) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "SystemExclusive from: " + sender.getDeviceName() + ", data:" + Arrays.toString(systemExclusive)));
        }

        @Override
        public void onMidiNoteOff(@NonNull MidiInputDevice sender, int channel, int note, int velocity) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "NoteOff from: " + sender.getDeviceName() + " channel: " + channel + ", note: " + note + ", velocity: " + velocity));

            if (velocity > 0) {
                soundPlayer.playMidiNote(note);
            }
        }

        @Override
        public void onMidiNoteOn(@NonNull MidiInputDevice sender, int channel, int note, int velocity) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "NoteOn from: " + sender.getDeviceName() + " channel: " + channel + ", note: " + note + ", velocity: " + velocity));
        }

        @Override
        public void onMidiPolyphonicAftertouch(@NonNull MidiInputDevice sender, int channel, int note, int pressure) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "PolyphonicAftertouch  from: " + sender.getDeviceName() + " channel: " + channel + ", note: " + note + ", pressure: " + pressure));
        }

        @Override
        public void onMidiControlChange(@NonNull MidiInputDevice sender, int channel, int function, int value) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "ControlChange from: " + sender.getDeviceName() + ", channel: " + channel + ", function: " + function + ", value: " + value));
        }

        @Override
        public void onMidiProgramChange(@NonNull MidiInputDevice sender, int channel, int program) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "ProgramChange from: " + sender.getDeviceName() + ", channel: " + channel + ", program: " + program));
        }

        @Override
        public void onMidiChannelAftertouch(@NonNull MidiInputDevice sender, int channel, int pressure) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "ChannelAftertouch from: " + sender.getDeviceName() + ", channel: " + channel + ", pressure: " + pressure));
        }

        @Override
        public void onMidiPitchWheel(@NonNull MidiInputDevice sender, int channel, int amount) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "PitchWheel from: " + sender.getDeviceName() + ", channel: " + channel + ", amount: " + amount));
        }

        @Override
        public void onMidiTimeCodeQuarterFrame(@NonNull MidiInputDevice sender, int timing) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "TimeCodeQuarterFrame from: " + sender.getDeviceName() + ", timing: " + timing));
        }

        @Override
        public void onMidiSongSelect(@NonNull MidiInputDevice sender, int song) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "SongSelect from: " + sender.getDeviceName() + ", song: " + song));
        }

        @Override
        public void onMidiSongPositionPointer(@NonNull MidiInputDevice sender, int position) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "SongPositionPointer from: " + sender.getDeviceName() + ", position: " + position));
        }

        @Override
        public void onMidiTuneRequest(@NonNull MidiInputDevice sender) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "TuneRequest from: " + sender.getDeviceName()));
        }

        @Override
        public void onMidiTimingClock(@NonNull MidiInputDevice sender) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "TimingClock from: " + sender.getDeviceName()));
        }

        @Override
        public void onMidiStart(@NonNull MidiInputDevice sender) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "Start from: " + sender.getDeviceName()));
        }

        @Override
        public void onMidiContinue(@NonNull MidiInputDevice sender) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "Continue from: " + sender.getDeviceName()));
        }

        @Override
        public void onMidiStop(@NonNull MidiInputDevice sender) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "Stop from: " + sender.getDeviceName()));
        }

        @Override
        public void onMidiActiveSensing(@NonNull MidiInputDevice sender) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "ActiveSensing from: " + sender.getDeviceName()));
        }

        @Override
        public void onMidiReset(@NonNull MidiInputDevice sender) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "Reset from: " + sender.getDeviceName()));
        }

        @Override
        public void onRPNMessage(@NonNull MidiInputDevice sender, int channel, int function, int value) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "RPN message from: " + sender.getDeviceName() + ", channel: " + channel + ", function: " + function + ", value: " + value));
        }

        @Override
        public void onNRPNMessage(@NonNull MidiInputDevice sender, int channel, int function, int value) {
            midiInputEventHandler.sendMessage(Message.obtain(midiInputEventHandler, 0, "NRPN message from: " + sender.getDeviceName() + ", channel: " + channel + ", function: " + function + ", value: " + value));
        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView midiInputEventListView = (ListView) findViewById(R.id.midiInputEventListView);
        midiInputEventAdapter = new ArrayAdapter<>(this, R.layout.midi_event, R.id.midiEventDescriptionTextView);
        midiInputEventListView.setAdapter(midiInputEventAdapter);

        deviceSpinner = (Spinner) findViewById(R.id.deviceNameSpinner);
        connectedDevicesAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.simple_spinner_dropdown_item, android.R.id.text1, new ArrayList<MidiInputDevice>());
        deviceSpinner.setAdapter(connectedDevicesAdapter);

        soundPlayer = SoundPlayer.getInstance(getApplicationContext());
        soundPlayer.setMappedInstrument(Instruments.get(Instruments.PANFLUTE));

        View.OnTouchListener onToneButtonTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int note = 60 + Integer.parseInt((String) v.getTag());

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    default:
                        // do nothing.
                        break;
                }
                return false;
            }
        };

        findViewById(R.id.buttonC).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonCis).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonD).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonDis).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonE).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonF).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonFis).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonG).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonGis).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonA).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonAis).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonB).setOnTouchListener(onToneButtonTouchListener);
        findViewById(R.id.buttonC2).setOnTouchListener(onToneButtonTouchListener);

        findViewById(R.id.buttonC).getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonCis).getBackground().setColorFilter(BLACK_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonD).getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonDis).getBackground().setColorFilter(BLACK_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonE).getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonF).getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonFis).getBackground().setColorFilter(BLACK_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonG).getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonGis).getBackground().setColorFilter(BLACK_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonA).getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonAis).getBackground().setColorFilter(BLACK_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonB).getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);
        findViewById(R.id.buttonC2).getBackground().setColorFilter(WHITE_KEY_COLOR, PorterDuff.Mode.MULTIPLY);

        Button disconnectButton = (Button) findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MidiInputDevice bleMidiDeviceFromSpinner = getBleMidiDeviceFromSpinner();
                if (bleMidiDeviceFromSpinner != null) {
                    bleMidiCentralProvider.disconnectDevice(bleMidiDeviceFromSpinner);
                }
            }
        });

        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CentralActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        if (!BleUtils.isBluetoothEnabled(this)) {
            BleUtils.enableBluetooth(this);
            return;
        }

        if (!BleUtils.isBleSupported(this)) {
            // display alert and exit
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Not supported");
            alertDialog.setMessage("Bluetooth LE is not supported on this device. The app will exit.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            });
            alertDialog.show();
        } else {
            setupCentralProvider();
        }
    }

    /**
     * Configure BleMidiCentralProvider instance
     */
    private void setupCentralProvider() {
        bleMidiCentralProvider = new BleMidiCentralProvider(this);

        bleMidiCentralProvider.setOnMidiDeviceAttachedListener(new OnMidiDeviceAttachedListener() {
            @Override
            public void onMidiInputDeviceAttached(@NonNull MidiInputDevice midiInputDevice) {
                midiInputDevice.setOnMidiInputEventListener(onMidiInputEventListener);

                Message message = new Message();
                message.arg1 = MIDI_DEVICE_ATTACHED;
                message.obj = midiInputDevice;
                midiConnectionChangedHandler.sendMessage(message);
            }

            @Override
            public void onMidiOutputDeviceAttached(@NonNull MidiOutputDevice midiOutputDevice) {
            }
        });

        bleMidiCentralProvider.setOnMidiDeviceDetachedListener(new OnMidiDeviceDetachedListener() {
            @Override
            public void onMidiInputDeviceDetached(@NonNull MidiInputDevice midiInputDevice) {
                // do nothing
                Message message = new Message();
                message.arg1 = MIDI_DEVICE_DETACHED;
                message.obj = midiInputDevice;
                midiConnectionChangedHandler.sendMessage(message);
            }

            @Override
            public void onMidiOutputDeviceDetached(@NonNull MidiOutputDevice midiOutputDevice) {
            }
        });

        bleMidiCentralProvider.setOnMidiScanStatusListener(new OnMidiScanStatusListener() {
            @Override
            public void onMidiScanStatusChanged(boolean isScanning) {
                CentralActivity.this.isScanning = isScanning;
                if (toggleScanMenu != null) {
                    if (isScanning) {
                        toggleScanMenu.setTitle(R.string.stop_scan);
                    } else {
                        toggleScanMenu.setTitle(R.string.start_scan);
                    }
                }
            }
        });

        // scan devices for 30 seconds
        bleMidiCentralProvider.startScanDevice(BLE_SCAN_TIME_MS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BleUtils.REQUEST_CODE_BLUETOOTH_ENABLE) {
            if (!BleUtils.isBluetoothEnabled(this)) {
                // User selected NOT to use Bluetooth.
                // do nothing
                return;
            }

            if (!BleUtils.isBleSupported(this)) {
                // display alert and exit
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Not supported");
                alertDialog.setMessage("Bluetooth LE is not supported on this device. The app will exit.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
                alertDialog.show();
            } else {
                setupCentralProvider();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bleMidiCentralProvider != null) {
            bleMidiCentralProvider.terminate();
        }
    }
}
