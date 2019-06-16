package com.example.heoju.sd4_project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends ActionBarActivity implements View.OnClickListener,
        TextToSpeech.OnInitListener {
    public ImageButton btnAircon, btnLED, btnWindow, btnSecure, btnDoor, btnAllOFF, btnSTT;
    static final int REQUEST_ENABLE_BT = 10;
    private static final int CODE_RECOG = 1214;
    BluetoothAdapter mBluetoothAdapter;
    int mPairedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;
    BluetoothDevice mRemoteDevice;
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;
    TextToSpeech tts;
    Thread mWorkerThread = null;
    Thread sttThread = null;
    String mStrDelimiter = "\n";
    char mCharDelimiter = '\n';
    byte[] readBuffer;
    int readBufferPosition;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    // 블루투스가 활성 상태로 변경됨
                    selectDevice();
                } else if (resultCode == RESULT_CANCELED) {
                    // 블루투스가 비활성 상태임
                    finish();    // 어플리케이션 종료
                }
                break;
            case CODE_RECOG:
                ArrayList<String> arList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String sRecog = arList.get(0);
                if (sRecog.equals("선풍기 켜 줘") == true) {
                    speakStr("선풍기를 키겠습니다.");
                    try {
                        Thread.sleep(1000); // in msec
                        sendData("3");
                        Switch switchFan = (Switch) findViewById(R.id.switchFan);
                        switchFan = (Switch) findViewById(R.id.switchFan);
                        if (switchFan.isChecked()) {
                            switchFan.setChecked(false);
                        } else {
                            switchFan.setChecked(true);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                } else if (sRecog.equals("선풍기 꺼 줘") == true) {
                    speakStr("선풍기를 끄겠습니다.");
                    try {
                        Thread.sleep(1000); // in msec
                        sendData("4");
                        Switch switchFan = (Switch) findViewById(R.id.switchFan);
                        if (switchFan.isChecked()) {
                            switchFan.setChecked(false);
                        } else {
                            switchFan.setChecked(true);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                } else if (sRecog.equals("창문 열어 줘") == true) {
                    speakStr("창문을 열겠습니다.");
                    try {
                        Thread.sleep(1000); // in msec
                        sendData("6");
                        Switch switchWindow = (Switch) findViewById(R.id.switchWindow);
                        if (switchWindow.isChecked()) {
                            switchWindow.setChecked(false);
                        } else {
                            switchWindow.setChecked(true);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                } else if (sRecog.equals("창문 닫아 줘") == true) {
                    speakStr("창문을 닫겠습니다.");
                    try {
                        Thread.sleep(1000); // in msec
                        sendData("7");
                        Switch switchWindow = (Switch) findViewById(R.id.switchWindow);
                        if (switchWindow.isChecked()) {
                            switchWindow.setChecked(false);
                        } else {
                            switchWindow.setChecked(true);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                } else if (sRecog.equals("불 켜 줘") == true) {
                    speakStr("전등을 키겠습니다.");
                    try {
                        Thread.sleep(1000); // in msec
                        sendData("1");
                        Switch switchLED = (Switch) findViewById(R.id.switchLED);
                        if (switchLED.isChecked()) {
                            switchLED.setChecked(false);
                        } else {
                            switchLED.setChecked(true);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                } else if (sRecog.equals("불 꺼 줘") == true) {
                    speakStr("전등을 끄겠습니다.");
                    try {
                        Thread.sleep(1000); // in msec
                        sendData("2");
                        Switch switchLED = (Switch) findViewById(R.id.switchLED);
                        if (switchLED.isChecked()) {
                            switchLED.setChecked(false);
                        } else {
                            switchLED.setChecked(true);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                } else if (sRecog.equals("문 열어 줘") == true) {
                    speakStr("출입구 잠금장치가 해제됩니다.");
                    try {
                        Thread.sleep(1000); // in msec
                        sendData("8");
                        Switch switchDoor = (Switch) findViewById(R.id.switchDoor);
                        if (switchDoor.isChecked()) {
                            switchDoor.setChecked(false);
                        } else {
                            switchDoor.setChecked(true);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;

                } else if (sRecog.equals("문 잠궈 줘") == true) {
                    speakStr("출입구 잠금장치를 설정합니다.");
                    try {
                        Thread.sleep(1000); // in msec
                        sendData("9");
                        Switch switchDoor = (Switch) findViewById(R.id.switchDoor);
                        if (switchDoor.isChecked()) {
                            switchDoor.setChecked(false);
                        } else {
                            switchDoor.setChecked(true);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;


                } else {
                    speakStr("잘 알아듣지 못했어요. 다시 말씀해 주세요.");
                    try {
                        Thread.sleep(1000); // in msec
                        voiceRecog(CODE_RECOG);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    //블루투스 지원 여부 확인
    void checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // 장치가 블루투스를 지원하지 않는 경우
            finish();    // 어플리케이션 종료
        } else {
            // 장치가 블루투스를 지원하는 경우
            if (!mBluetoothAdapter.isEnabled()) {
                // 블루투스를 지원하지만 비활성 상태인 경우
                // 블루투스를 활성 상태로 바꾸기 위해 사용자 동의 요청
                Intent enableBtIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                // 블루투스를 지원하며 활성 상태인 경우
                // 페어링 된 기기 목록을 보여주고 연결할 장치를 선택
                selectDevice();
            }
        }
    }

    void selectDevice() { //연결 설정
        mDevices = mBluetoothAdapter.getBondedDevices(); //페어링된 장치 목록은 블루투스 어댑터 함수 사용
        mPairedDeviceCount = mDevices.size();

        if (mPairedDeviceCount == 0) {
            // 페어링 된 장치가 없는 경우
            finish();        // 어플리케이션 종료
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        // 페어링 된 블루투스 장치의 이름 목록 작성
        List<String> listItems = new ArrayList<String>();
        for (BluetoothDevice device : mDevices) {
            listItems.add(device.getName()); //장비의 이름을 가져와서 추가
        }
        listItems.add("취소");        // 취소 항목 추가

        final CharSequence[] items =
                listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == mPairedDeviceCount) {
                    // 연결할 장치를 선택하지 않고 ‘취소’를 누른 경우
                    finish();
                } else {
                    // 연결할 장치를 선택한 경우
                    // 선택한 장치와 연결을 시도함
                    connectToSelectedDevice(items[item].toString());

                }
            }
        });

        builder.setCancelable(false);    // 뒤로 가기 버튼 사용 금지
        AlertDialog alert = builder.create();
        alert.show();
    }

    void sttForData() {
        final Handler handler = new Handler();

        sttThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                String str = "명령어를 말해주세요.";
                                speakStr(str);

                                voiceRecog(CODE_RECOG);
                            }
                        });

                    } catch (Exception e) {
                        finish();
                        e.printStackTrace();
                    }
                    break;
                }
            }
        });

        sttThread.start();

    }

    void beginListenForData() { //수신은 쓰레드를 계속 돌려서 데이터가 들어오는지 확인해야한다. 송신은 버튼이벤트로 처리되어 있다.
        final Handler handler = new Handler();

        readBuffer = new byte[1024];    // 수신 버퍼
        readBufferPosition = 0;        // 버퍼 내 수신 문자 저장 위치

        // 문자열 수신 쓰레드
        mWorkerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted()) { //인터럽트 상황이 발생하면 종료
                    try {
                        int bytesAvailable = mInputStream.available();    // 수신 데이터 확인
                        if (bytesAvailable > 0) {        // 데이터가 수신된 경우
                            byte[] packetBytes = new byte[bytesAvailable]; //들어온 스트림단위를 바이트단위로 변환?
                            mInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == mCharDelimiter) { //종료 문자가 들어올때까지
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0,
                                            encodedBytes, 0, encodedBytes.length); //바이트 단위로 인코딩
                                    final String data = new String(encodedBytes, "UTF-8");
                                    readBufferPosition = 0; //한번 데이터를 다 읽고 난 다음 버퍼를 초기화 시키는것

                                    handler.post(new Runnable() {
                                                     public void run() {
                                                         int variable = Integer.parseInt(data);
                                                         if (variable >= 0 && variable <= 100) {

                                                             TextView temp = (TextView) findViewById(R.id.temp);
                                                             temp.setText("온도 : " + data + "°C");

                                                             // SeekBar bar = (SeekBar) findViewById(R.id.seekBar);
                                                             // bar.setProgress(variable);

                                                         }
                                                     }
                                                 }
                                    );
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        // 데이터 수신 중 오류 발생
                        finish();
                    }
                }
            }
        });
        mWorkerThread.start();
    }

    void sendData(String msg) {
        msg += mStrDelimiter;    // 문자열 종료 표시 \n or \0  ex)hello를 버퍼로 한글자씩 읽고 마지막으로 \n를 붙혀보내 문장의 끝을 알린다.
        try {
            mOutputStream.write(msg.getBytes()); // 문자열 전송
        } catch (Exception e) {
            // 문자열 전송 도중 오류가 발생한 경우
            finish();        // 어플리케이션 종료
        }
    }

    //페어링된 블루투스 장치를 이름으로 찾기
    BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;

        for (BluetoothDevice device : mDevices) {
            if (name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }

    @Override
    protected void onDestroy() {
        try {
            mWorkerThread.interrupt();    // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
        } catch (Exception e) {
        }

        super.onDestroy();
    }

    void connectToSelectedDevice(String selectedDeviceName) {
        mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);
        //UUID 지정 : 블루투스 장비의 고유 번호를 가지고 소켓을 만드는 것으로 설정해야한다.
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            // UUID 코드를 가지고 소켓 생성
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
            // RFCOMM 채널을 통한 연결
            mSocket.connect();

            // 데이터 송수신을 위한 스트림 얻기
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();

            // 데이터 수신 준비
            beginListenForData();
        } catch (Exception e) {
            // 블루투스 연결 중 오류 발생
            finish();        // 어플리케이션 종료
        }
    }

    @SuppressLint("NewApi")
    private void speakStr(String str) {
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);
        while (tts.isSpeaking()) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.KOREAN);
            tts.setPitch(1.0f);
            tts.setSpeechRate(1.0f);
        }
    }

    private void voiceRecog(int nCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "말해주세요");
        startActivityForResult(intent, nCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Switch switchFan, switchLED, switchWindow, switchSecure, switchDoor, switchAllOFF;
        ImageButton imgBtn;
        ImageButton btnSTT;

        checkBluetooth();
        tts = new TextToSpeech(this, this);
        imgBtn = (ImageButton) findViewById(R.id.btnAircon);
        imgBtn.setOnClickListener(this);

        imgBtn = (ImageButton) findViewById(R.id.btnLED);
        imgBtn.setOnClickListener(this);

        imgBtn = (ImageButton) findViewById(R.id.btnDoor);
        imgBtn.setOnClickListener(this);

        imgBtn = (ImageButton) findViewById(R.id.btnSecure);
        imgBtn.setOnClickListener(this);

        imgBtn = (ImageButton) findViewById(R.id.btnWindow);
        imgBtn.setOnClickListener(this);

        imgBtn = (ImageButton) findViewById(R.id.btnAllOFF);
        imgBtn.setOnClickListener(this);

        imgBtn = (ImageButton) findViewById(R.id.btnSTT);
        imgBtn.setOnClickListener(this);
        btnSTT = (ImageButton) findViewById(R.id.btnSTT);
        btnSTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sttForData();

            }
        });

        switchFan = (Switch) findViewById(R.id.switchFan);
        switchFan.setClickable(false);
        switchLED = (Switch) findViewById(R.id.switchLED);
        switchLED.setClickable(false);
        switchWindow = (Switch) findViewById(R.id.switchWindow);
        switchWindow.setClickable(false);
        switchSecure = (Switch) findViewById(R.id.switchSecure);
        switchSecure.setClickable(false);
        switchDoor = (Switch) findViewById(R.id.switchDoor);
        switchDoor.setClickable(false);
        switchAllOFF = (Switch) findViewById(R.id.switchAllOFF);
        switchAllOFF.setClickable(false);
    }

    @Override
    public void onClick(View v) {
        String str = new String();
        Switch switchFan, switchLED, switchWindow, switchSecure, switchDoor, switchAllOFF;
        switchFan = (Switch) findViewById(R.id.switchFan);
        switchLED = (Switch) findViewById(R.id.switchLED);
        switchWindow = (Switch) findViewById(R.id.switchWindow);
        switchSecure = (Switch) findViewById(R.id.switchSecure);
        switchDoor = (Switch) findViewById(R.id.switchDoor);
        switch (v.getId()) {
            case R.id.btnAllOFF:
                str = "0";
                switchLED.setChecked(false);
                switchFan.setChecked(false);
                switchSecure.setChecked(false);
                switchWindow.setChecked(false);
                switchDoor.setChecked(false);

                break;

            case R.id.btnLED:
                str = "1";
                switchLED = (Switch) findViewById(R.id.switchLED);
                if (switchLED.isChecked()) {
                    switchLED.setChecked(false);
                } else {
                    switchLED.setChecked(true);
                }
                break;

            case R.id.btnAircon:
                str = "3";
                switchFan = (Switch) findViewById(R.id.switchFan);
                if (switchFan.isChecked()) {
                    switchFan.setChecked(false);
                } else {
                    switchFan.setChecked(true);
                }
                break;

            case R.id.btnSecure:
                str = "5";
                switchSecure = (Switch) findViewById(R.id.switchSecure);

                if (switchSecure.isChecked()) {
                    switchSecure.setChecked(false);
                } else {
                    switchSecure.setChecked(true);
                }

                break;

            case R.id.btnWindow:
                str = "6";
                switchWindow = (Switch) findViewById(R.id.switchWindow);
                if (switchWindow.isChecked()) {
                    switchWindow.setChecked(false);
                } else {
                    switchWindow.setChecked(true);
                }

                break;

            case R.id.btnDoor:
                str = "8";
                switchDoor = (Switch) findViewById(R.id.switchDoor);
                if (switchDoor.isChecked()) {
                    switchDoor.setChecked(false);
                } else {
                    switchDoor.setChecked(true);
                }
                break;


        }

        sendData(str);

    }
}