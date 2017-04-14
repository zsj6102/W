package per.lijuan.meituan.Activity;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import omrecorder.AudioChunk;
import omrecorder.AudioSource;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.Recorder;
import per.lijuan.meituan.Adapter.DragGridAdapter;
import per.lijuan.meituan.Adapter.GridAdapter;
import per.lijuan.meituan.Bean.BottomCom;
import per.lijuan.meituan.Bean.Command;
import per.lijuan.meituan.HttpThread.SendText;
import per.lijuan.meituan.HttpThread.SendWav;
import per.lijuan.meituan.Interface.AddClickListener;
import per.lijuan.meituan.Interface.CoItemListener;
import per.lijuan.meituan.Interface.MoveSwapListener;
import per.lijuan.meituan.Interface.PostCallBack;
import per.lijuan.meituan.View.DragGridView;
import per.lijuan.meituan.Interface.MoveBottomListener;
import per.lijuan.meituan.Interface.SubClickListener;
import per.lijuan.meituan.R;
import per.lijuan.meituan.View.FileDialog;
import per.lijuan.meituan.View.MyDialog;

import static per.lijuan.meituan.Util.AndroidUtil.isNetworkAvailable;


public class MainActivity extends Activity implements View.OnClickListener, RealmChangeListener<Realm> {
    List<Map<String, Object>> mList;
    List<Map<String, Object>> list;
    List<Command> mmlist;
    Map<Integer, Integer> resmap;
    List<BottomCom> bottomList;
    List<Map<String, Object>> coli;
    private int count = 0;
    private TextView edit_tv;
    private TextView edit_com;
    private boolean isShowDelete = false;
    private GridAdapter mAdapter;
    private GridView grid;
    private DragGridView dragGridView;
    private DragGridView fileGridView;
    private DragGridAdapter dragGridAdapter;
    private DragGridAdapter fileAdapter;
    private Realm realm;
    private int dilogpos = -1;
    private String name;
    private String command;
    private TextView tv_back;
    private MyDialog dialog;
    private FileDialog fileDialog;
    Recorder recorder;
    private boolean isInFile = false;
    private int[] res = {R.mipmap.bottom0, R.mipmap.bottom1, R.mipmap.bottom2, R.mipmap.bottom3, R.mipmap.bottom4, R.mipmap.bottom5, R.mipmap.bottom6,
            R.mipmap.bottom7, R.mipmap.bottom8};
    private int[] check = {R.mipmap.check0, R.mipmap.check1, R.mipmap.check2, R.mipmap.check3, R.mipmap.check4, R.mipmap.check5, R.mipmap.check6, R.mipmap.check7, R.mipmap.check8};
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("msg.what", msg.what + "");
            switch (msg.what) {
                case 0:
                    Map<String, String> map = new HashMap<>();
                    map = (Map<String, String>) msg.obj;
                    if (map.get("status").equals("true")) {
                        RealmResults<Command> userList = realm.where(Command.class).findAll();
                        for(int i = 0;i<userList.size();i++){
                            if(userList.get(i).getCom().equals(command)){
                                Toast.makeText(MainActivity.this, "您已添加过该命令", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        for(int i = 0;i<userList.size();i++){
                            if(userList.get(i).getId()>=count){
                                count = userList.get(i).getId();
                            }
                        }

                        realm.beginTransaction();
                        int id = count + 1;
                        Log.e("id",id+"");
                        boolean isFolder;
                        isFolder = false;
                        Command com = realm.createObject(Command.class);
                        com.setId(id);
                        com.setFilePosition(-1);
                        com.setCom(command);
                        com.setRes(res[dilogpos]);
                        com.setName(name);
                        com.setFolder(false);
                        com.setSwaped(false);
                        realm.commitTransaction();
                        Map<String, Object> maps = new HashMap<>();
                        maps.put("ItemImage", res[dilogpos]);
                        maps.put("ItemText", name);
                        maps.put("ItemCommand", command);
                        maps.put("isFolder", isFolder);
                        maps.put("filePosition", -1);
                        maps.put("id", id);
                        maps.put("swaped", false);
                        mList.add(maps);
                        dragGridAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {

                        Toast.makeText(MainActivity.this, "不存在该指令", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    Map<String, String> wavmap = new HashMap<>();
                    wavmap = (Map<String, String>) msg.obj;
                    if (wavmap.get("status").equals("true")) {
//                        editText.setText(wavmap.get("info"));
                        if (dialog != null) {
                            dialog.setCom(wavmap.get("info"));
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "不存在该语音指令", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 3:
                    Map<String, String> maps = new HashMap<>();
                    maps = (Map<String, String>) msg.obj;
                    if (maps.get("status").equals("true")) {
                        Toast.makeText(MainActivity.this, "控制成功", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 4:
                    Map<String, Object> mapc = new HashMap<>();
                    mapc = (Map<String, Object>) msg.obj;
                    if (mapc.get("status").equals("true")) {
                        RealmResults<Command> userList = realm.where(Command.class).findAll();
                        for(int i = 0;i<userList.size();i++){
                            if(userList.get(i).getCom().equals(command)){
                                Toast.makeText(MainActivity.this, "您已添加过该命令", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        for(int i = 0;i<userList.size();i++){
                            if(userList.get(i).getId()>=count){
                                count = userList.get(i).getId();
                            }
                        }
                        realm.beginTransaction();
                        int id = count+ 1;
                        boolean isFolder;
                        isFolder = false;
                        Command com = realm.createObject(Command.class);
                        com.setId(id);
                        com.setFilePosition((int) mapc.get("filePosition"));
                        com.setCom(command);
                        com.setRes(res[dilogpos]);
                        com.setName(name);
                        com.setFolder(false);
                        com.setSwaped(true);
                        realm.commitTransaction();
                        Map<String, Object> mapo = new HashMap<>();
                        mapo.put("ItemImage", res[dilogpos]);
                        mapo.put("ItemText", name);
                        mapo.put("ItemCommand", command);
                        mapo.put("isFolder", isFolder);
                        mapo.put("filePositon", (int) mapc.get("filePosition"));
                        mapo.put("id", id);
                        mapo.put("swaped", true);
                        coli.add(mapo);
                        fileAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, "不存在该指令", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 5:
                    Map<String, String> mapmodi = new HashMap<>();
                    mapmodi = (Map<String, String>) msg.obj;
                    if (mapmodi.get("status").equals("true")) {

                       final int pos = Integer.parseInt(mapmodi.get("pos"));
                        RealmResults<Command> userList = realm.where(Command.class).findAll();
                        for(int i = 0;i<userList.size();i++){
                            if(userList.get(i).getId()!=(int)mList.get(pos).get("id")){
                                if(userList.get(i).getCom().equals(command)){
                                    Toast.makeText(MainActivity.this, "您已添加过该命令", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                        }
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Command comma = realm.where(Command.class).equalTo("id", (int) mList.get(pos).get("id")).findFirst();
                                if (comma != null) {
                                    comma.setCom(command);
                                    comma.setRes(res[dilogpos]);
                                    comma.setName(name);
                                }
                            }
                        });
                        mList.get(pos).put("ItemImage",res[dilogpos]);
                        mList.get(pos).put("ItemText",name);
                        mList.get(pos).put("ItemCommand",command);
                        dragGridAdapter.notifyDataSetChanged();
                        for(int i = 0;i<list.size();i++){
                            if(list.get(i).get("id") == mList.get(pos).get("id")){
                                list.get(i).put("ItemImage",res[dilogpos]);
                                list.get(i).put("ItemText",name);
                                list.get(i).put("ItemCommand",command);

                            }
                        }
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                BottomCom comma = realm.where(BottomCom.class).equalTo("id", (int) mList.get(pos).get("id")).findFirst();
                                if (comma != null) {
                                    comma.setCom(command);
                                    comma.setRes(res[dilogpos]);
                                    comma.setName(name);
                                }
                            }
                        });
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    break;
                case 6:
                    Map<String, String> mfm = new HashMap<>();
                    mfm = (Map<String, String>) msg.obj;
                    if (mfm.get("status").equals("true")) {

                        final int pos = Integer.parseInt(mfm.get("pos"));
                        RealmResults<Command> userList = realm.where(Command.class).findAll();
                        for(int i = 0;i<userList.size();i++){
                            if(userList.get(i).getId()!=(int)coli.get(pos).get("id")){
                                if(userList.get(i).getCom().equals(command)){
                                    Toast.makeText(MainActivity.this, "您已添加过该命令", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                        }
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Command comma = realm.where(Command.class).equalTo("id", (int) coli.get(pos).get("id")).findFirst();
                                if (comma != null) {
                                    comma.setCom(command);
                                    comma.setRes(res[dilogpos]);
                                    comma.setName(name);
                                }
                            }
                        });
                        coli.get(pos).put("ItemImage",res[dilogpos]);
                        coli.get(pos).put("ItemText",name);
                        coli.get(pos).put("ItemCommand",command);
                        fileAdapter.notifyDataSetChanged();
                        for(int i = 0;i<list.size();i++){
                            if(list.get(i).get("id") == coli.get(pos).get("id")){
                                list.get(i).put("ItemImage",res[dilogpos]);
                                list.get(i).put("ItemText",name);
                                list.get(i).put("ItemCommand",command);

                            }
                        }
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                BottomCom comma = realm.where(BottomCom.class).equalTo("id", (int) coli.get(pos).get("id")).findFirst();
                                if (comma != null) {
                                    comma.setCom(command);
                                    comma.setRes(res[dilogpos]);
                                    comma.setName(name);
                                }
                            }
                        });
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    break;
                default:
                    break;
            }

        }
    };
    int[] imgres = {R.mipmap.bottom0, R.mipmap.bottom1, R.mipmap.bottom2, R.mipmap.bottom2, R.mipmap.bottom3, R.mipmap.bottom4, R.mipmap.bottom5, R.mipmap.bottom6, R.mipmap.bottom7, R.mipmap.bottom8};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = new ArrayList<>();
        list = new ArrayList<>();
        coli = new ArrayList<>();
        resmap = new HashMap<>();
        Log.e("count",count+"");
        realm = Realm.getDefaultInstance();
        setupRecorder();
        RealmResults<Command> dogs = realm.where(Command.class).findAll();
        mmlist = realm.copyFromRealm(dogs);
        RealmResults<BottomCom> bottom = realm.where(BottomCom.class).findAll();
        bottomList = realm.copyFromRealm(bottom);
        int size = mmlist.size();
        for (int i = 0; i < size; i++) {
            if (mmlist.get(i).isFolder()) {
                mmlist.add(0, mmlist.get(i));
                mmlist.remove(i + 1);
            }

        }
        for (int i = 0; i < 9; i++) {

            resmap.put(res[i], check[i]);
        }
        for (int i = 0; i < size; i++) {

            Map<String, Object> map = new HashMap<>();

            map.put("id", mmlist.get(i).getId());
            map.put("ItemImage", mmlist.get(i).getRes());
            map.put("ItemText", mmlist.get(i).getName());
            map.put("ItemCommand", mmlist.get(i).getCom());
            map.put("isFolder", mmlist.get(i).isFolder());
            map.put("filePosition", mmlist.get(i).getFilePosition());
            map.put("swaped", mmlist.get(i).isSwaped());
            mList.add(map);
        }
        for (int i = 0; i < bottomList.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", bottomList.get(i).getId());
            map.put("ItemImage", bottomList.get(i).getRes());
            map.put("ItemText", bottomList.get(i).getName());
            map.put("ItemCommand", bottomList.get(i).getCom());
            map.put("isFolder", bottomList.get(i).isFolder());
            map.put("filePosition", bottomList.get(i).getFilePosition());
            map.put("swaped", bottomList.get(i).isSwaped());
            list.add(map);
        }

        setContentView(R.layout.activity_main);
        initView();

        adp();


    }

    public List<Map<String, Object>> getList() {
//        int size = mList.size();
//        for(int i = 0;i<size;i++){
//            if((boolean)mList.get(i).get("swaped")){
//                mList.remove(i);
//            }
//        }
//        return  mList;
        Iterator<Map<String, Object>> it = mList.iterator();
        while (it.hasNext()) {
            Map<String, Object> x = it.next();
            if ((boolean) x.get("swaped")) {
                it.remove();
            }
        }
        return mList;
    }

    private void initView() {
        edit_tv = (TextView) findViewById(R.id.bianji);
        edit_tv.setOnClickListener(this);
        edit_com = (TextView) findViewById(R.id.kongzhi);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);
    }

    /**
     * 初始化数据源
     */

    private void adp() {
        mAdapter = new GridAdapter(this, list);


        dragGridAdapter = new DragGridAdapter(this, getList());


        dragGridAdapter.setBottomListener(new MoveBottomListener() {
            @Override
            public void moveBottom(int pos) {


                if(list!=null){
                    for(int i=0;i<list.size();i++){
                        if(list.get(i).get("ItemCommand").equals(mList.get(pos).get("ItemCommand"))){
                            Toast.makeText(MainActivity.this, "不能重复添加", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                if (pos != mList.size()) {
                    if ((boolean) mList.get(pos).get("isFolder")) {
                        Toast.makeText(MainActivity.this, "文件夹不能添加到快捷", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (list.size() >= 5) {
                        Toast.makeText(MainActivity.this, "最多添加5个快捷按钮", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    if (!isExist(list, mList, pos)) {
                    if (list.size() < 5 && pos != mList.size()) {

                        list.add(mList.get(pos));
                        realm.beginTransaction();

                        BottomCom com = realm.createObject(BottomCom.class);
                        com.setFilePosition((int)mList.get(pos).get("filePosition"));
                        com.setSwaped((boolean)mList.get(pos).get("swaped"));
                        com.setFolder((boolean)mList.get(pos).get("isFolder"));
                        com.setId((int) mList.get(pos).get("id"));
                        com.setCom(mList.get(pos).get("ItemCommand").toString());
                        com.setRes((int) mList.get(pos).get("ItemImage"));
                        com.setName(mList.get(pos).get("ItemText").toString());
                        realm.commitTransaction();
                        mAdapter.notifyDataSetChanged();
                    }

//                    } else {
//                        Toast.makeText(MainActivity.this, "已经存在不能重复添加", Toast.LENGTH_SHORT).show();
//                    }

                }


            }
        });

        dragGridAdapter.setMoveSwapListener(new MoveSwapListener() {
                                                @Override
                                                public void swap(final int oldpos, final int newpos) {

                                                    if (!(boolean) mList.get(newpos).get("isFolder")) {

                                                        Toast.makeText(MainActivity.this, "必须要文件夹", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    } else {
                                                        if (oldpos != mList.size()) {

                                                            realm.executeTransaction(new Realm.Transaction() {
                                                                @Override
                                                                public void execute(Realm realm) {
                                                                    Command command = realm.where(Command.class).equalTo("id", (int) mList.get(oldpos).get("id")).findFirst();
                                                                    if (command != null) {
                                                                        command.setFilePosition((int) mList.get(newpos).get("filePosition"));
                                                                        command.setSwaped(true);
                                                                    }
                                                                }
                                                            });
                                                            mList.remove(oldpos);
                                                            dragGridAdapter.notifyDataSetChanged();
                                                            Toast.makeText(MainActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                                                        }


                                                    }

                                                }
                                            }

        );
        dragGridAdapter.setClicker(new

                                           SubClickListener() {
                                               @Override
                                               public void click(View view, final int pos) {
                                                   final RealmResults<Command> userList = realm.where(Command.class).findAll();
                                                   final RealmResults<BottomCom> bolist = realm.where(BottomCom.class).findAll();
                                                   realm.executeTransaction(new Realm.Transaction() {

                                                       @Override
                                                       public void execute(Realm realm) {
                                                           Log.e("click", pos + "");
                                                           int dpos = 0;
                                                           List<Command> delist;
                                                           delist = realm.copyFromRealm(userList);

                                                           if ((boolean) mList.get(pos).get("isFolder")) {
                                                               for (int i = 0; i < delist.size(); i++) {
                                                                   if (delist.get(i).getFilePosition() == (int) mList.get(pos).get("filePosition")) {
                                                                       if (!delist.get(i).isFolder()) {
//                                                                           for (int m = 0; m < bolist.size(); m++) {
//                                                                               if (userList.get(i).getId() == bolist.get(m).getId()) {
//                                                                                   bolist.get(m).deleteFromRealm();
//                                                                               }
//                                                                           }
                                                                           bolist.deleteAllFromRealm();
                                                                           for (int m = 0; m < list.size(); m++) {
                                                                               if (userList.get(i).getId() == (int) list.get(m).get("id")) {
                                                                                   list.remove(m);
                                                                               }
                                                                           }
                                                                       }
                                                                   }
                                                                   if(userList.get(i).getFilePosition() == (int)mList.get(pos).get("filePosition")){
                                                                       userList.get(i).deleteFromRealm();
                                                                   }

                                                               }
                                                           } else {
                                                               for (int i = 0; i < delist.size(); i++) {
                                                                   if (delist.get(i).getId() == (int) mList.get(pos).get("id")) {
                                                                       dpos = i;
                                                                       if (!delist.get(i).isFolder()) {
                                                                           bolist.deleteAllFromRealm();
                                                                           for (int m = 0; m < list.size(); m++) {
                                                                               if (userList.get(i).getId() == (int) list.get(m).get("id")) {
                                                                                   list.remove(m);
                                                                               }
                                                                           }
                                                                       }

                                                                   }

                                                               }
                                                               userList.get(dpos).deleteFromRealm();
                                                           }


                                                       }
                                                   });
                                                   if(list.size()!=0){
                                                       for(int i = 0;i<list.size();i++){
                                                           realm.beginTransaction();

                                                           BottomCom com = realm.createObject(BottomCom.class);
                                                           com.setFilePosition((int)list.get(i).get("filePosition"));
                                                           com.setSwaped((boolean)list.get(i).get("swaped"));
                                                           com.setFolder((boolean)list.get(i).get("isFolder"));
                                                           com.setId((int) list.get(i).get("id"));
                                                           com.setCom(list.get(i).get("ItemCommand").toString());
                                                           com.setRes((int) list.get(i).get("ItemImage"));
                                                           com.setName(list.get(i).get("ItemText").toString());
                                                           realm.commitTransaction();
                                                       }
                                                   }


                                                   mList.remove(pos);
                                                   mAdapter.notifyDataSetChanged();
                                                   dragGridAdapter.notifyDataSetChanged();
                                               }
                                           }

        );
        dragGridAdapter.setCoItemListener(new

                                                  CoItemListener() {
                                                      @Override
                                                      public void click(final int pos) {

                                                          if ((boolean) mList.get(pos).get("isFolder")) {
                                                              if (!isShowDelete) {
                                                                  isInFile = true;
                                                                  Toast.makeText(MainActivity.this, "isFolder", Toast.LENGTH_SHORT).show();
                                                                  fileGridView = (DragGridView) findViewById(R.id.fileGridView);
                                                                  dragGridView.setVisibility(View.GONE);
                                                                  fileGridView.setVisibility(View.VISIBLE);
                                                                  edit_com.setText("群组");
                                                                  Log.e("mList", mList.size() + "");
                                                                  List<Command> comlist = new ArrayList<Command>();

                                                                  RealmResults<Command> dogs = realm.where(Command.class).findAll();
                                                                  comlist = realm.copyFromRealm(dogs);
                                                                  final int fi = (int) mList.get(pos).get("filePosition");
                                                                  Log.e("fi", fi + "");
                                                                  coli.clear();
                                                                  for (int i = 0; i < comlist.size(); i++) {
                                                                      if ((int) mList.get(pos).get("filePosition") == comlist.get(i).getFilePosition()) {
                                                                          Map<String, Object> map = new HashMap<String, Object>();
                                                                          if (!comlist.get(i).isFolder()) {
                                                                              String name = comlist.get(i).getName();
                                                                              map.put("id", comlist.get(i).getId());
                                                                              map.put("ItemImage", comlist.get(i).getRes());
                                                                              map.put("ItemText", comlist.get(i).getName());
                                                                              map.put("ItemCommand", comlist.get(i).getCom());
                                                                              map.put("isFolder", comlist.get(i).isFolder());
                                                                              map.put("swaped",comlist.get(i).isSwaped());
                                                                              map.put("filePosition", comlist.get(i).getFilePosition());
                                                                              coli.add(map);
                                                                          }

                                                                      }
                                                                  }
                                                                  Log.e("fileSize", coli.size() + "");
                                                                  fileAdapter = new DragGridAdapter(MainActivity.this, coli);
                                                                  fileAdapter.setClicker(new SubClickListener() {
                                                                      @Override
                                                                      public void click(View view, final int pos) {
                                                                          final RealmResults<Command> userList = realm.where(Command.class).findAll();
                                                                          final RealmResults<BottomCom> bolist = realm.where(BottomCom.class).findAll();
                                                                          realm.executeTransaction(new Realm.Transaction() {

                                                                              @Override
                                                                              public void execute(Realm realm) {
                                                                                  Log.e("click", pos + "");
                                                                                  int dpos = 0;
                                                                                  List<Command> delist;
                                                                                  delist = realm.copyFromRealm(userList);

                                                                                  for (int i = 0; i < delist.size(); i++) {
                                                                                      if (delist.get(i).getId() == (int) coli.get(pos).get("id")) {
                                                                                          dpos = i;
                                                                                          for (int m = 0; m < bolist.size(); m++) {
                                                                                              if (userList.get(i).getId() == bolist.get(m).getId()) {
                                                                                                  bolist.get(m).deleteFromRealm();
                                                                                              }
                                                                                          }
                                                                                          for (int m = 0; m < list.size(); m++) {
                                                                                              if (userList.get(i).getId() == (int) list.get(m).get("id")) {
                                                                                                  list.remove(m);
                                                                                              }
                                                                                          }
                                                                                      }

                                                                                  }
                                                                                  userList.get(dpos).deleteFromRealm();
                                                                              }
                                                                          });
                                                                          coli.remove(pos);
                                                                          mAdapter.notifyDataSetChanged();
                                                                          fileAdapter.notifyDataSetChanged();
                                                                      }
                                                                  });
                                                                  fileAdapter.setBottomListener(new MoveBottomListener() {
                                                                      @Override
                                                                      public void moveBottom(int pos) {
                                                                          if(list.size()>0){
                                                                              for(int i=0;i<list.size();i++){
                                                                                  if(list.get(i).get("ItemCommand").equals(coli.get(pos).get("ItemCommand"))){
                                                                                      Toast.makeText(MainActivity.this, "不能重复添加", Toast.LENGTH_SHORT).show();
                                                                                      return;
                                                                                  }
                                                                              }
                                                                          }
                                                                          if (pos != coli.size()) {

                                                                              if (list.size() >= 5) {
                                                                                  Toast.makeText(MainActivity.this, "最多添加5个快捷按钮", Toast.LENGTH_SHORT).show();
                                                                                  return;
                                                                              }
                                                                              if (list.size() < 5 ) {
                                                                                  list.add(coli.get(pos));
                                                                                  realm.beginTransaction();

                                                                                  BottomCom com = realm.createObject(BottomCom.class);
                                                                                  com.setFilePosition((int)coli.get(pos).get("filePosition"));
                                                                                  com.setSwaped((boolean)coli.get(pos).get("swaped"));
                                                                                  com.setFolder((boolean)coli.get(pos).get("isFolder"));
                                                                                  com.setId((int) coli.get(pos).get("id"));
                                                                                  com.setCom(coli.get(pos).get("ItemCommand").toString());
                                                                                  com.setRes((int) coli.get(pos).get("ItemImage"));
                                                                                  com.setName(coli.get(pos).get("ItemText").toString());
                                                                                  realm.commitTransaction();
                                                                                  mAdapter.notifyDataSetChanged();
                                                                              }

                                                                          }
                                                                      }
                                                                  });
                                                                  fileAdapter.setCoItemListener(new CoItemListener() {
                                                                      @Override
                                                                      public void click(final int pos) {
                                                                          if (!isShowDelete) {
                                                                              if (resmap.get(coli.get(pos).get("ItemImage")) != null) {
                                                                                  coli.get(pos).put("ItemImage", resmap.get(coli.get(pos).get("ItemImage")));
                                                                                  for (int i = 0; i < coli.size(); i++) {
                                                                                      if (i != pos) {
                                                                                          for (int m = 0; m < check.length; m++) {
                                                                                              if ((int) coli.get(i).get("ItemImage") == check[m]) {
                                                                                                  coli.get(i).put("ItemImage", res[m]);
                                                                                              }
                                                                                          }

                                                                                      }
                                                                                  }
                                                                                  fileAdapter.notifyDataSetChanged();
                                                                              }
                                                                              CheckNet();
                                                                              SendText.send((String) coli.get(pos).get("ItemCommand"), new PostCallBack() {
                                                                                  @Override
                                                                                  public void excute(String str) {
                                                                                      try {
                                                                                          JSONObject jsonObject = new JSONObject(str);
                                                                                          Message msg = new Message();
                                                                                          msg.what = 3;
                                                                                          Map<String, String> map = new HashMap<String, String>();
                                                                                          map.put("status", jsonObject.get("status").toString());
                                                                                          if (jsonObject.get("status").toString().equals("true")) {
                                                                                              map.put("info", jsonObject.get("info").toString());
                                                                                          }
                                                                                          msg.obj = map;
                                                                                          MainActivity.this.handler.sendMessage(msg);
                                                                                      } catch (JSONException e) {
                                                                                          e.printStackTrace();
                                                                                      }
                                                                                  }
                                                                              });
                                                                          }else{
                                                                              dialog = new MyDialog(MainActivity.this, R.style.mydialog);
                                                                              dialog.SetFile(true);
                                                                              dialog.setInEdit(true);
                                                                              dialog.show();
                                                                              dialog.setCom((String)coli.get(pos).get("ItemCommand"));
                                                                              dialog.setName((String)coli.get(pos).get("ItemText"));
                                                                              for(int i = 0;i<res.length;i++){
                                                                                  if((int)coli.get(pos).get("ItemImage") == res[i]){
                                                                                      dialog.getAdapter().setSelection(i);
                                                                                      dilogpos = i;
                                                                                  }
                                                                              }
                                                                              dialogLogic();
                                                                              dialog.setSureButtonListener(new View.OnClickListener() {
                                                                                  @Override
                                                                                  public void onClick(View v) {
                                                                                      name = dialog.getName();
                                                                                      command = dialog.getCom();
                                                                                      boolean havename = "".equals(name);
                                                                                      boolean havaecommand = "".equals(command);
                                                                                      if (!dialog.isFolder()) {
                                                                                          if ("".equals(name)) {
                                                                                              Toast.makeText(MainActivity.this, "请输入名称", Toast.LENGTH_SHORT).show();
                                                                                              return;
                                                                                          }
                                                                                          if ("".equals(command)) {
                                                                                              Toast.makeText(MainActivity.this, "请输入指令", Toast.LENGTH_SHORT).show();
                                                                                              return;
                                                                                          }
                                                                                          if (dilogpos == -1) {
                                                                                              Toast.makeText(MainActivity.this, "请选择图标", Toast.LENGTH_SHORT).show();
                                                                                              return;
                                                                                          }
                                                                                          if (!havaecommand && !havename && dilogpos != -1) {
                                                                                              CheckNet();
                                                                                              SendText.send(command, new PostCallBack() {
                                                                                                  @Override
                                                                                                  public void excute(String str) {
                                                                                                      try {
                                                                                                          JSONObject jsonObject = new JSONObject(str);
                                                                                                          Message msg = new Message();
                                                                                                          msg.what = 6;//修改按钮
                                                                                                          Map<String, String> map = new HashMap<String, String>();
                                                                                                          map.put("status", jsonObject.get("status").toString());
                                                                                                          if (jsonObject.get("status").toString().equals("true")) {
                                                                                                              map.put("info", jsonObject.get("info").toString());
                                                                                                          }
                                                                                                          map.put("pos",pos+"");
                                                                                                          msg.obj = map;
                                                                                                          MainActivity.this.handler.sendMessage(msg);
                                                                                                      } catch (JSONException e) {
                                                                                                          e.printStackTrace();
                                                                                                      }
                                                                                                  }
                                                                                              });

                                                                                          }
                                                                                      }
                                                                                  }
                                                                              });
                                                                          }
                                                                      }
                                                                  });
                                                                  fileAdapter.setAddClickListener(new AddClickListener() {
                                                                      @Override
                                                                      public void addItem() {
                                                                          dialog = new MyDialog(MainActivity.this, R.style.mydialog);
                                                                          dialog.SetFile(true);
                                                                          dialog.show();
                                                                          dialogLogic();
                                                                          dialog.setSureButtonListener(new View.OnClickListener() {
                                                                              @Override
                                                                              public void onClick(View v) {
                                                                                  name = dialog.getName();
                                                                                  command = dialog.getCom();
                                                                                  boolean havename = "".equals(name);
                                                                                  boolean havaecommand = "".equals(command);
                                                                                  if ("".equals(name)) {
                                                                                      Toast.makeText(MainActivity.this, "请输入名称", Toast.LENGTH_SHORT).show();
                                                                                      return;
                                                                                  }
                                                                                  if ("".equals(command)) {
                                                                                      Toast.makeText(MainActivity.this, "请输入指令", Toast.LENGTH_SHORT).show();
                                                                                      return;
                                                                                  }
                                                                                  if (dilogpos == -1) {
                                                                                      Toast.makeText(MainActivity.this, "请选择图标", Toast.LENGTH_SHORT).show();
                                                                                      return;
                                                                                  }
                                                                                  if (!havaecommand && !havename && dilogpos != -1) {
                                                                                      CheckNet();
                                                                                      SendText.send(command, new PostCallBack() {
                                                                                          @Override
                                                                                          public void excute(String str) {
                                                                                              try {
                                                                                                  JSONObject jsonObject = new JSONObject(str);
                                                                                                  Message msg = new Message();
                                                                                                  msg.what = 4;
                                                                                                  Map<String, Object> map = new HashMap<String, Object>();
                                                                                                  map.put("status", jsonObject.get("status").toString());
                                                                                                  map.put("filePosition", fi);
                                                                                                  if (jsonObject.get("status").toString().equals("true")) {
                                                                                                      map.put("info", jsonObject.get("info").toString());
                                                                                                  }
                                                                                                  msg.obj = map;
                                                                                                  MainActivity.this.handler.sendMessage(msg);
                                                                                              } catch (JSONException e) {
                                                                                                  e.printStackTrace();
                                                                                              }
                                                                                          }
                                                                                      });

                                                                                  }
                                                                              }
                                                                          });
                                                                      }
                                                                  });
                                                                  fileGridView.setAdapter(fileAdapter);
                                                              }else{
                                                               fileDialog = new FileDialog(MainActivity.this, R.style.mydialog);
                                                                  fileDialog.show();
                                                                  fileDialog.setSureButtonListener(new View.OnClickListener() {
                                                                      @Override
                                                                      public void onClick(View v) {

                                                                          if ("".equals(fileDialog.getName())) {
                                                                              Toast.makeText(MainActivity.this, "请输入文件名称", Toast.LENGTH_SHORT).show();
                                                                              return;
                                                                          }
                                                                          realm.executeTransaction(new Realm.Transaction() {
                                                                              @Override
                                                                              public void execute(Realm realm) {
                                                                                  Command comma = realm.where(Command.class).equalTo("id", (int) mList.get(pos).get("id")).findFirst();
                                                                                  if (comma != null) {
                                                                                      comma.setName(fileDialog.getName());
                                                                                  }
                                                                              }
                                                                          });
                                                                          mList.get(pos).put("ItemText",fileDialog.getName());
                                                                          dragGridAdapter.notifyDataSetChanged();
                                                                          Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                                                          fileDialog.dismiss();

                                                                      }
                                                                  });
                                                              }

                                                          } else {
                                                              //按钮非编辑模式下
                                                              if (!isShowDelete) {
                                                                  if (resmap.get(mList.get(pos).get("ItemImage")) != null) {
                                                                      mList.get(pos).put("ItemImage", resmap.get(mList.get(pos).get("ItemImage")));
                                                                      for (int i = 0; i < mList.size(); i++) {
                                                                          if (i != pos) {
                                                                              for (int m = 0; m < check.length; m++) {
                                                                                  if ((int) mList.get(i).get("ItemImage") == check[m]) {
                                                                                      mList.get(i).put("ItemImage", res[m]);
                                                                                  }
                                                                              }

                                                                          }
                                                                      }
                                                                      dragGridAdapter.notifyDataSetChanged();
                                                                  }
                                                                  CheckNet();
                                                                  SendText.send((String) mList.get(pos).get("ItemCommand"), new PostCallBack() {
                                                                      @Override
                                                                      public void excute(String str) {
                                                                          try {
                                                                              JSONObject jsonObject = new JSONObject(str);
                                                                              Message msg = new Message();
                                                                              msg.what = 3;
                                                                              Map<String, String> map = new HashMap<String, String>();
                                                                              map.put("status", jsonObject.get("status").toString());
                                                                              if (jsonObject.get("status").toString().equals("true")) {
                                                                                  map.put("info", jsonObject.get("info").toString());
                                                                              }
                                                                              msg.obj = map;
                                                                              MainActivity.this.handler.sendMessage(msg);
                                                                          } catch (JSONException e) {
                                                                              e.printStackTrace();
                                                                          }
                                                                      }
                                                                  });
                                                              }else{//按钮编辑模式下
                                                                  dialog = new MyDialog(MainActivity.this, R.style.mydialog);
                                                                  dialog.SetFile(true);
                                                                  dialog.setInEdit(true);
                                                                  dialog.show();
                                                                  dialog.setCom((String)mList.get(pos).get("ItemCommand"));
                                                                  dialog.setName((String)mList.get(pos).get("ItemText"));
                                                                  for(int i = 0;i<res.length;i++){
                                                                      if((int)mList.get(pos).get("ItemImage") == res[i]){
                                                                          dialog.getAdapter().setSelection(i);
                                                                          dilogpos = i;
                                                                      }
                                                                  }

                                                                  dialogLogic();


                                                                  dialog.setSureButtonListener(new View.OnClickListener() {
                                                                      @Override
                                                                      public void onClick(View v) {
                                                                          name = dialog.getName();
                                                                          command = dialog.getCom();
                                                                          boolean havename = "".equals(name);
                                                                          boolean havaecommand = "".equals(command);
                                                                          if (!dialog.isFolder()) {
                                                                              if ("".equals(name)) {
                                                                                  Toast.makeText(MainActivity.this, "请输入名称", Toast.LENGTH_SHORT).show();
                                                                                  return;
                                                                              }
                                                                              if ("".equals(command)) {
                                                                                  Toast.makeText(MainActivity.this, "请输入指令", Toast.LENGTH_SHORT).show();
                                                                                  return;
                                                                              }
                                                                              if (dilogpos == -1) {
                                                                                  Toast.makeText(MainActivity.this, "请选择图标", Toast.LENGTH_SHORT).show();
                                                                                  return;
                                                                              }
                                                                              if (!havaecommand && !havename && dilogpos != -1) {
                                                                                  CheckNet();
                                                                                  SendText.send(command, new PostCallBack() {
                                                                                      @Override
                                                                                      public void excute(String str) {
                                                                                          try {
                                                                                              JSONObject jsonObject = new JSONObject(str);
                                                                                              Message msg = new Message();
                                                                                              msg.what = 5;//修改按钮
                                                                                              Map<String, String> map = new HashMap<String, String>();
                                                                                              map.put("status", jsonObject.get("status").toString());
                                                                                              if (jsonObject.get("status").toString().equals("true")) {
                                                                                                  map.put("info", jsonObject.get("info").toString());
                                                                                              }
                                                                                              map.put("pos",pos+"");
                                                                                              msg.obj = map;
                                                                                              MainActivity.this.handler.sendMessage(msg);
                                                                                          } catch (JSONException e) {
                                                                                              e.printStackTrace();
                                                                                          }
                                                                                      }
                                                                                  });

                                                                              }
                                                                          }
                                                                      }
                                                                  });

                                                              }
                                                          }
                                                      }
                                                  }

        );

        dragGridAdapter.setAddClickListener(new

                                                    AddClickListener() {
                                                        @Override
                                                        public void addItem() {
                                                            //添加对话框

                                                            dialog = new MyDialog(MainActivity.this, R.style.mydialog);
                                                            dialog.SetFile(false);


                                                            dialog.show();
                                                            dialogLogic();
                                                            dialog.setSureButtonListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    name = dialog.getName();
                                                                    command = dialog.getCom();
                                                                    boolean havename = "".equals(name);
                                                                    boolean havaecommand = "".equals(command);
                                                                    if (!dialog.isFolder()) {
                                                                        if ("".equals(name)) {
                                                                            Toast.makeText(MainActivity.this, "请输入名称", Toast.LENGTH_SHORT).show();
                                                                            return;
                                                                        }
                                                                        if ("".equals(command)) {
                                                                            Toast.makeText(MainActivity.this, "请输入指令", Toast.LENGTH_SHORT).show();
                                                                            return;
                                                                        }
                                                                        if (dilogpos == -1) {
                                                                            Toast.makeText(MainActivity.this, "请选择图标", Toast.LENGTH_SHORT).show();
                                                                            return;
                                                                        }
                                                                        if (!havaecommand && !havename && dilogpos != -1) {
                                                                            CheckNet();
                                                                            SendText.send(command, new PostCallBack() {
                                                                                @Override
                                                                                public void excute(String str) {
                                                                                    try {
                                                                                        JSONObject jsonObject = new JSONObject(str);
                                                                                        Message msg = new Message();
                                                                                        msg.what = 0;
                                                                                        Map<String, String> map = new HashMap<String, String>();
                                                                                        map.put("status", jsonObject.get("status").toString());
                                                                                        if (jsonObject.get("status").toString().equals("true")) {
                                                                                            map.put("info", jsonObject.get("info").toString());
                                                                                        }
                                                                                        msg.obj = map;
                                                                                        MainActivity.this.handler.sendMessage(msg);
                                                                                    } catch (JSONException e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                }
                                                                            });

                                                                        }
                                                                    }
                                                                    if (dialog.isFolder()) {
                                                                        if ("".equals(dialog.getFolderName())) {
                                                                            Toast.makeText(MainActivity.this, "请输入文件名称", Toast.LENGTH_SHORT).show();
                                                                            return;
                                                                        }
                                                                        realm.beginTransaction();

                                                                        RealmResults<Command> userList = realm.where(Command.class).findAll();
                                                                        for(int i = 0;i<userList.size();i++){
                                                                            if(userList.get(i).getId()>=count){
                                                                                count = userList.get(i).getId();
                                                                            }
                                                                        }
                                                                        int id = count + 1;
                                                                        Log.e("fileid",id+"");
                                                                        boolean isFolder;
                                                                        isFolder = true;
                                                                        Command com = realm.createObject(Command.class);
                                                                        com.setId(id);
                                                                        com.setCom("");
                                                                        com.setFilePosition(id * 20);
                                                                        com.setRes(R.mipmap.folder);
                                                                        com.setName(dialog.getFolderName());
                                                                        com.setFolder(isFolder);
                                                                        com.setSwaped(false);
                                                                        realm.commitTransaction();
                                                                        Map<String, Object> maps = new HashMap<>();
                                                                        maps.put("id", id);
                                                                        maps.put("ItemImage", R.mipmap.folder);
                                                                        maps.put("ItemText", dialog.getFolderName());
                                                                        maps.put("ItemCommand", "");
                                                                        maps.put("isFolder", isFolder);
                                                                        maps.put("filePosition", id * 20);
                                                                        maps.put("swaped", false);
                                                                        mList.add(0, maps);
                                                                        dragGridAdapter.notifyDataSetChanged();
                                                                        dialog.dismiss();
                                                                    }
                                                                }
                                                            });


                                                        }
                                                    }

        );
        mAdapter.setClicker(new

                                    SubClickListener() {
                                        @Override
                                        public void click(View view, final int pos) {
                                            final RealmResults<BottomCom> userList = realm.where(BottomCom.class).findAll();
                                            realm.executeTransaction(new Realm.Transaction() {
                                                @Override
                                                public void execute(Realm realm) {
                                                    userList.get(pos).deleteFromRealm();
                                                }
                                            });
                                            list.remove(pos);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }

        );

        grid = (GridView)

                findViewById(R.id.mGridViewPager1);

        dragGridView = (DragGridView)

                findViewById(R.id.mGridViewPager);

        dragGridView.setAdapter(dragGridAdapter);
        grid.setAdapter(mAdapter);
        realm.addChangeListener(this);
    }

    private void CheckNet(){
        if(!isNetworkAvailable(MainActivity.this)){
            Toast.makeText(MainActivity.this,"网络不可用，请检查！",Toast.LENGTH_SHORT).show();
            return;
        }
    }
    private void dialogLogic() {
        dialog.setVoiceTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        recorder.startRecording();
                        dialog.getEdt_com().requestFocus();
                        dialog.getEdt_name().clearFocus();
                        break;
                    case MotionEvent.ACTION_UP:
                        recorder.stopRecording();
                        File file = new File(Environment.getExternalStorageDirectory() + "/" + "zsj.wav");
                        CheckNet();
                        SendWav.send(file, new PostCallBack() {
                            @Override
                            public void excute(String str) {
                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    Message msg = new Message();
                                    msg.what = 1;
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("status", jsonObject.get("status").toString());
                                    if (jsonObject.get("status").toString().equals("true")) {
                                        map.put("info", jsonObject.get("info").toString());

//                                                dialog.setCom(edit_comm);
                                    }
                                    msg.obj = map;
                                    MainActivity.this.handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, position + "", Toast.LENGTH_SHORT).show();
                dialog.getAdapter().setSelection(position);
                dilogpos = position;
                dialog.getAdapter().notifyDataSetChanged();
            }
        });
        dialog.setEdtNameClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.getEdt_name().requestFocus();
            }
        });

    }

    private void setupRecorder() {
        recorder = OmRecorder.wav(
                new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
                    @Override
                    public void onAudioChunkPulled(AudioChunk audioChunk) {
                        animateVoice((float) (audioChunk.maxAmplitude()) / 2);
                    }
                }), file());
    }

    private void animateVoice(final float maxPeak) {

    }

    private AudioSource mic() {
        return new AudioSource.Smart(MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                AudioFormat.CHANNEL_IN_MONO, 44100);
    }

    @NonNull
    private File file() {
        return new File(Environment.getExternalStorageDirectory(), "zsj.wav");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bianji:
                if (edit_tv.getText() == "完成") {
                    edit_tv.setText("编辑");
                } else {
                    edit_tv.setText("完成");
                }
                if (isShowDelete) {

                    isShowDelete = false;
                    mAdapter.setIsShowDelete(isShowDelete);
                    dragGridAdapter.setIsShowDelete(isShowDelete);
                    if (fileAdapter != null) {
                        fileAdapter.setIsShowDelete(isShowDelete);
                    }

                } else {

                    isShowDelete = true;
                    mAdapter.setIsShowDelete(isShowDelete);
                    dragGridAdapter.setIsShowDelete(isShowDelete);
                    if (fileAdapter != null) {
                        fileAdapter.setIsShowDelete(isShowDelete);
                    }

                }

                break;
            case R.id.kongzhi:

                break;
            case R.id.tv_back:
                if (isInFile) {
                    dragGridView.setVisibility(View.VISIBLE);
                    fileGridView.setVisibility(View.GONE);
                    isInFile = false;
                    edit_com.setText("智能控制");
                } else {
                    Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                }

                break;

        }
    }

    @Override
    public void onChange(Realm element) {
        findAll();
    }

    private void findAll() {
    }

    @Override
    protected void onDestroy() {

        realm.close();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(MainActivity.this, ShowActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
