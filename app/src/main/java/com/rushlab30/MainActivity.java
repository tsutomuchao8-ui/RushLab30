package com.rushlab30;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Typeface;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import org.json.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    private final String PREF = "rushlab30_data";
    private LinearLayout root, content;
    private String page = "home";
    private JSONArray notes;
    private EditText coinPosition, coinColor, coinDirection, noteTitle, noteBody;
    private Spinner noteType;
    private TextView status;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(0xFF101827);
        getWindow().setNavigationBarColor(0xFF101827);
        try { notes = new JSONArray(getPreferences(0).getString(PREF, "[]")); }
        catch(Exception e) { notes = new JSONArray(); }
        render();
    }
    int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + .5f); }
    TextView text(String s, int size, int color) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        t.setPadding(dp(2),dp(6),dp(2),dp(6)); return t;
    }
    LinearLayout column() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(1); l.setPadding(dp(16),dp(10),dp(16),dp(16)); return l;
    }
    void addLine(LinearLayout box, String s) { TextView t=text(s,14,0xFF334155); box.addView(t); }
    Button button(String s, Runnable r) {
        Button b = new Button(this); b.setText(s); b.setAllCaps(false);
        b.setOnClickListener(v -> r.run()); return b;
    }
    EditText edit(String hint) {
        EditText e = new EditText(this); e.setSingleLine(false); e.setHint(hint);
        e.setTextSize(15); e.setPadding(dp(12),dp(8),dp(12),dp(8)); return e;
    }
    void render() {
        root = new LinearLayout(this); root.setOrientation(1); root.setBackgroundColor(0xFFF4F7FB);
        LinearLayout header = new LinearLayout(this); header.setOrientation(1); header.setPadding(dp(18),dp(14),dp(18),dp(12)); header.setBackgroundColor(0xFF101827);
        TextView brand=text("RUSHLAB 30",20,0xFFFFFFFF); brand.setTypeface(null,Typeface.BOLD); header.addView(brand);
        header.addView(text("公开线索研究记录 · 本地保存",12,0xFFB9C6D8)); root.addView(header);
        HorizontalScrollView navScroll=new HorizontalScrollView(this); LinearLayout nav=new LinearLayout(this);
        nav.addView(button("概览",()->go("home"))); nav.addView(button("16 枚硬币",()->go("coins")));
        nav.addView(button("线索档案",()->go("notes"))); navScroll.addView(nav); root.addView(navScroll);
        ScrollView scroll=new ScrollView(this); content=column(); scroll.addView(content); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        status=text("只记录公开画面证据；不保存助记词、私钥或钱包密码。",11,0xFF64748B);
        status.setPadding(dp(16),dp(8),dp(16),dp(10)); root.addView(status);
        setContentView(root);
        if(page.equals("home")) home(); else if(page.equals("coins")) coins(); else notesPage();
    }
    void go(String p) { page=p; render(); }
    void title(String s) { TextView t=text(s,22,0xFF0F172A); t.setTypeface(null,Typeface.BOLD); content.addView(t); }
    void home() {
        title("研究工作台");
        addLine(content,"项目：RushWallet Puzzle #30");
        addLine(content,"目标：整理视频中可观察、可复核的线索，不尝试访问钱包或获取资产。");
        content.addView(button("记录 16 枚硬币",()->go("coins")));
        content.addView(button("新增一条线索",()->go("notes")));
        addLine(content,"当前记录数："+notes.length());
        addLine(content,"研究纪律：");
        addLine(content,"• 事实：画面或公开来源能直接确认的内容");
        addLine(content,"• 假设：对线索含义的解释，必须标注为未验证");
        addLine(content,"• 待核实：需要原始视频、清晰帧或独立来源");
        addLine(content,"安全边界：本工具不含助记词猜测、私钥推导、密码爆破或钱包导入功能。");
    }
    void coins() {
        title("16 枚硬币观察表");
        addLine(content,"逐枚记录画面位置与外观。先描述，不急着解释数字含义。");
        coinPosition=edit("位置（例：左上、中心偏右）"); content.addView(coinPosition);
        coinColor=edit("外观 / 颜色（例：金色、银色、反光）"); content.addView(coinColor);
        coinDirection=edit("朝向 / 特征（例：正面、反面、边缘朝向）"); content.addView(coinDirection);
        content.addView(button("保存为观察记录",()->{
            String p=val(coinPosition), c=val(coinColor), d=val(coinDirection);
            if(p.isEmpty() && c.isEmpty() && d.isEmpty()){toast("至少填写一项观察内容");return;}
            addNote("硬币观察","位置："+p+"；外观："+c+"；朝向/特征："+d,"事实（待截图复核）");
            toast("已保存观察记录"); go("coins");
        }));
        addLine(content,"已记录的硬币观察：");
        for(int i=0;i<notes.length();i++){
            JSONObject o=notes.optJSONObject(i); if(o!=null && "硬币观察".equals(o.optString("category")))
                addLine(content,"• "+o.optString("body"));
        }
    }
    String val(EditText e){return e.getText().toString().trim();}
    void notesPage() {
        title("线索档案");
        noteTitle=edit("标题（例：视频 00:42 的白板）"); content.addView(noteTitle);
        noteType=new Spinner(this);
        String[] types={"事实（可直接观察）","假设（尚未验证）","待核实（缺少证据）"};
        ArrayAdapter<String> ad=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,types);
        noteType.setAdapter(ad); content.addView(noteType);
        noteBody=edit("记录内容：时间戳、画面描述、来源、疑点……"); content.addView(noteBody);
        content.addView(button("保存线索",()->{
            if(val(noteTitle).isEmpty() || val(noteBody).isEmpty()){toast("请填写标题和内容");return;}
            addNote(val(noteTitle),val(noteBody),noteType.getSelectedItem().toString());
            ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(noteBody.getWindowToken(),0);
            toast("线索已保存"); go("notes");
        }));
        content.addView(button("导出研究记录 JSON",()->exportData()));
        addLine(content,"已保存线索（最新在前）：");
        for(int i=notes.length()-1;i>=0;i--){
            JSONObject o=notes.optJSONObject(i); if(o==null) continue;
            TextView h=text(o.optString("title")+" · "+o.optString("type"),15,0xFF0F172A); h.setTypeface(null,Typeface.BOLD); content.addView(h);
            addLine(content,o.optString("body"));
            addLine(content,o.optString("time"));
            View line=new View(this); line.setBackgroundColor(0xFFDCE3EC); content.addView(line,new LinearLayout.LayoutParams(-1,dp(1)));
        }
    }
    void addNote(String title,String body,String type) {
        JSONObject o=new JSONObject();
        try {
            o.put("title",title); o.put("body",body); o.put("type",type);
            o.put("category",title.equals("硬币观察")?"硬币观察":"线索");
            o.put("time",new SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.getDefault()).format(new Date()));
            notes.put(o); getPreferences(0).edit().putString(PREF,notes.toString()).apply();
        } catch(Exception ignored){}
    }
    void exportData() {
        Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT); i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("application/json"); i.putExtra(Intent.EXTRA_TITLE,"RushLab30-research.json");
        startActivityForResult(i,30);
    }
    protected void onActivityResult(int req,int res,Intent data) {
        super.onActivityResult(req,res,data);
        if(req==30 && res==RESULT_OK && data!=null && data.getData()!=null) {
            try(java.io.OutputStream out=getContentResolver().openOutputStream(data.getData())) {
                out.write(notes.toString(2).getBytes("UTF-8")); toast("导出成功");
            } catch(Exception e){toast("导出失败："+e.getMessage());}
        }
    }
    void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
}
