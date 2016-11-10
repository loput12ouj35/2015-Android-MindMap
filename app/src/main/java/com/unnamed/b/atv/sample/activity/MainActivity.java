package com.unnamed.b.atv.sample.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.sample.R;
import com.unnamed.b.atv.sample.holder.IconTreeItemHolder;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.io.File;
import java.util.List;


public class MainActivity extends Activity {
    private static int RESULT_LOAD_IMAGE = 1;

    RelativeLayout mindmapLayout;

    private AndroidTreeView tView;
    private View tView_result;

    private TreeNode first_root;
    private TreeNode current_root;
    private EditText input;

    private String picturePath;
    private IconTreeItemHolder.IconTreeItem imageItem;

    private boolean disable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mindmapLayout = (RelativeLayout) findViewById(R.id.mindmapLayout);
        mindmapLayout.setX(mindmapLayout.getX() - (1920 - 1080));

        input = (EditText) findViewById(R.id.input);

        TreeNode root = TreeNode.root();
        TreeNode computerRoot = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_cloud, "Happiness"));
        first_root = computerRoot;
        current_root = first_root;

        TreeNode myDocuments = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Money"));
        TreeNode downloads = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "luxury"));
        TreeNode file1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "gold"));
        TreeNode file2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "sliver"));
        TreeNode file3 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "gem"));
        TreeNode file4 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "ivory"));
        downloads.addChildren(file1, file2, file3, file4);

        TreeNode myMedia = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo_library, "life"));
        TreeNode photo1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "health"));
        TreeNode photo2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "love"));
        TreeNode photo3 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_photo, "stability"));
        myMedia.addChildren(photo1, photo2, photo3);
        myDocuments.addChild(downloads);
        computerRoot.addChildren(myDocuments, myMedia);

        root.addChildren(computerRoot);

        /////////////////////////////////////////////////////////////////

        tView = new AndroidTreeView(this, root);
        tView.setDefaultAnimation(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);
        tView.setDefaultNodeClickListener(nodeClickListener);
        tView.setDefaultNodeLongClickListener(nodeLongClickListener);

        RelativeLayout containerView = (RelativeLayout) findViewById(R.id.item);
        tView_result = tView.getView();
        containerView.addView(tView_result);
////////////////////////////////////////////////////////////////////////////////////////
        setMView(first_root);
    }

    public void setMView (TreeNode root) {
        current_root = root;
        List<TreeNode> children = root.getChildren();

        mindmapLayout.removeAllViewsInLayout();

        final float x = 1080 / 2;
        final float y = 1080 / 2;
        float space = 50;

        TextView rootView = new TextView(this);
        rootView.setText(((IconTreeItemHolder.IconTreeItem) root.getValue()).text);
        rootView.setTextColor(Color.BLUE);
        rootView.setTextSize(50);
        rootView.setTypeface(null, Typeface.BOLD);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodeClick(mindmapLayout.indexOfChild(v));
            }
        });
        rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                nodeLongClick(mindmapLayout.indexOfChild(v), v, false);
                return false;
            }
        });

        mindmapLayout.addView(rootView);
        rootView.setX(700 - rootView.getMeasuredWidth() / 2);
        rootView.setY(400);


        for(int i = 0; i < children.size(); i++){
            TreeNode childNode = children.get(i);
            IconTreeItemHolder.IconTreeItem tmp = (IconTreeItemHolder.IconTreeItem) childNode.getValue();

            if(tmp.imagePath.equals("")) {
                TextView childView = new TextView(this);

                childView.setText(tmp.text);
                if(tmp.x == 0)
                    tmp.x = x;
                if(tmp.y == 0) {
                    tmp.y = y + space;
                    space += 100;
                }
                childView.setX(tmp.x);
                childView.setY(tmp.y);
                childView.setTextColor(tmp.color);
                childView.setTextSize(tmp.size);
                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nodeClick(mindmapLayout.indexOfChild(v));
                    }
                });
                childView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        nodeLongClick(mindmapLayout.indexOfChild(v), v, false);
                        return false;
                    }
                });
                mindmapLayout.addView(childView);
            } else {
                RelativeLayout.LayoutParams clp = new RelativeLayout.LayoutParams(tmp.size * 5, tmp.size * 5);
                ImageView childView = new ImageView(this);
                childView.setLayoutParams(clp);

                childView.setImageBitmap(BitmapFactory.decodeFile(tmp.imagePath));
                childView.setX(tmp.x);
                childView.setY(tmp.y);


                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nodeClick(mindmapLayout.indexOfChild(v));
                    }
                });
                childView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        nodeLongClick(mindmapLayout.indexOfChild(v), v, true);
                        return false;
                    }
                });
                mindmapLayout.addView(childView);
            }
        }

        for(int i = 0; i < mindmapLayout.getChildCount(); i++){
            View child = mindmapLayout.getChildAt(i);
            child.setEnabled(disable);
            child.setClickable(disable);
            child.setVisibility(disable? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        disableATV();

        if(disable)
            setMView(first_root);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            imageItem.imagePath = picturePath;
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.expandAll:
                tView.expandAll();
                break;

            case R.id.collapseAll:
                tView.collapseAll();
                break;
        }
        return true;
    }


    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            input.setText(item.text + "'s child");
        }
    };

    private TreeNode.TreeNodeLongClickListener nodeLongClickListener = new TreeNode.TreeNodeLongClickListener() {
        @Override
        public boolean onLongClick(TreeNode node, Object value) {
            nodeLongClick2(node);
            return true;
        }
    };


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", tView.getSaveState());
    }

    public String getInput() {
        return input.getText().toString();
    }

    public void disableATV() {
        input.setEnabled(disable);
        input.setClickable(disable);
        input.setVisibility(disable? View.VISIBLE : View.INVISIBLE);

        tView_result.setEnabled(disable);
        tView_result.setClickable(disable);
        tView_result.setVisibility(disable? View.VISIBLE : View.INVISIBLE);

        for(int i = 0; i < mindmapLayout.getChildCount(); i++){
            View child = mindmapLayout.getChildAt(i);
            child.setEnabled(!disable);
            child.setClickable(!disable);
            child.setVisibility(!disable? View.VISIBLE : View.INVISIBLE);
        }


        disable = !disable;
    }

    public void nodeClick (int index) {
        if(index == 0){     //clikced node is the root node
            if(current_root.getLevel() != 1)        //the root node is not overall root
                setMView(current_root.getParent());
        } else{
            setMView(current_root.getChildren().get(index - 1));
        }
    }


    public void nodeLongClick2 (TreeNode node) {
        final IconTreeItemHolder.IconTreeItem item =
                (IconTreeItemHolder.IconTreeItem) node.getValue();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater =
                (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View Viewlayout = inflater.inflate(R.layout.dialog_custom2,
                (ViewGroup) findViewById(R.id.layout_custom_dialog2));

        final EditText titleEdit = (EditText) Viewlayout.findViewById(R.id.title2);
        final EditText commentEdit = (EditText) Viewlayout.findViewById(R.id.comment2);

        final PrintView icon1 = (PrintView) Viewlayout.findViewById(R.id.icon1);
        final PrintView icon2 = (PrintView) Viewlayout.findViewById(R.id.icon2);
        final PrintView icon3 = (PrintView) Viewlayout.findViewById(R.id.icon3);
        final PrintView icon4 = (PrintView) Viewlayout.findViewById(R.id.icon4);

        final IconTreeItemHolder holder = (IconTreeItemHolder) (node.getViewHolder());

        titleEdit.setText(item.text);
        commentEdit.setText(item.comment);


        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.icon1:
                        item.icon = R.string.ic_drive_file;
                        break;
                    case R.id.icon2:
                        item.icon = R.string.ic_folder;
                        break;
                    case R.id.icon3:
                        item.icon = R.string.ic_photo;
                        break;
                    case R.id.icon4:
                        item.icon = R.string.ic_photo_library;
                        break;
                }
                holder.setIcon(item.icon);
            }
        };

        icon1.setOnClickListener(l);
        icon2.setOnClickListener(l);
        icon3.setOnClickListener(l);
        icon4.setOnClickListener(l);


        builder.setTitle("Node Edit").setView(Viewlayout).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                item.text = titleEdit.getText().toString();
                item.comment = commentEdit.getText().toString();
                holder.setText(item.text);
            }
        });
        builder.create().show();
    }


    public void nodeLongClick (final int index, final View v, final boolean isImage) {
        final TreeNode node = (index == 0)? current_root : current_root.getChildren().get(index - 1);
        final IconTreeItemHolder.IconTreeItem item =
                (IconTreeItemHolder.IconTreeItem) node.getValue();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater =
                (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View Viewlayout = inflater.inflate(R.layout.dialog_custom,
                (ViewGroup) findViewById(R.id.layout_custom_dialog));

        final TextView sizeTxt = (TextView) Viewlayout.findViewById(R.id.size_txt);

        final EditText titleEdit = (EditText) Viewlayout.findViewById(R.id.title);
        final EditText commentEdit = (EditText) Viewlayout.findViewById(R.id.comment);

        final SeekBar sizeBar = (SeekBar) Viewlayout.findViewById(R.id.size);

        final View colorRed = Viewlayout.findViewById(R.id.color_red);
        final View colorYellow = Viewlayout.findViewById(R.id.color_yellow);
        final View colorGreen = Viewlayout.findViewById(R.id.color_green);
        final View colorCyan = Viewlayout.findViewById(R.id.color_cyan);
        final View colorBlue = Viewlayout.findViewById(R.id.color_blue);
        final View colorMagenta = Viewlayout.findViewById(R.id.color_magenta);
        final View colorGray = Viewlayout.findViewById(R.id.color_gray);
        final View colorBlack = Viewlayout.findViewById(R.id.color_black);

        final IconTreeItemHolder holder = (IconTreeItemHolder) (node.getViewHolder());

        final EditText editX = (EditText) Viewlayout.findViewById(R.id.x);
        final EditText editY = (EditText) Viewlayout.findViewById(R.id.y);

        final Button upload = (Button) Viewlayout.findViewById(R.id.upload);
        final ImageView iv = (ImageView) Viewlayout.findViewById(R.id.imageTest);

        if(!item.imagePath.equals("")) {
            iv.setImageBitmap(BitmapFactory.decodeFile(item.imagePath));
        }
        sizeTxt.setText("Size : " + item.size);

        titleEdit.setText(item.text);
        titleEdit.setTextColor(item.color);
        commentEdit.setText(item.comment);

        editX.setText("" + item.x);
        editY.setText("" + item.y);

        sizeBar.setMax(50);
        sizeBar.setProgress(item.size);
        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress < 10) {
                    sizeBar.setProgress(10);
                    progress = 10;
                }
                sizeTxt.setText("Size : " + progress);
                item.size = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.color_red:
                        item.color = Color.RED;
                        break;
                    case R.id.color_yellow:
                        item.color = Color.YELLOW;
                        break;
                    case R.id.color_green:
                        item.color = Color.GREEN;
                        break;
                    case R.id.color_cyan:
                        item.color = Color.CYAN;
                        break;
                    case R.id.color_blue:
                        item.color = Color.BLUE;
                        break;
                    case R.id.color_magenta:
                        item.color = Color.MAGENTA;
                        break;
                    case R.id.color_gray:
                        item.color = Color.GRAY;
                        break;
                    case R.id.color_black:
                        item.color = Color.BLACK;
                        break;
                }

                titleEdit.setTextColor(item.color);
            }
        };

        colorRed.setOnClickListener(l);
        colorYellow.setOnClickListener(l);
        colorGreen.setOnClickListener(l);
        colorCyan.setOnClickListener(l);
        colorBlue.setOnClickListener(l);
        colorMagenta.setOnClickListener(l);
        colorGray.setOnClickListener(l);
        colorBlack.setOnClickListener(l);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                imageItem = item;
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


        builder.setTitle("Node Edit").setView(Viewlayout).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                item.text = titleEdit.getText().toString();
                item.comment = commentEdit.getText().toString();
                item.x = Float.parseFloat(editX.getText().toString());
                item.y = Float.parseFloat(editY.getText().toString());

                if(!isImage) {
                    TextView tmp = (TextView) v;
                    tmp.setText(item.text);

                    if (index != 0) {        //not root
                        tmp.setTextSize(item.size);
                        tmp.setTextColor(item.color);
                        tmp.setX(item.x);
                        tmp.setY(item.y);
                    }
                } else{
                    ImageView tmp = (ImageView) v;

                    RelativeLayout.LayoutParams clp = new RelativeLayout.LayoutParams(item.size * 5, item.size * 5);
                    tmp.setLayoutParams(clp);

                    tmp.setX(item.x);
                    tmp.setY(item.y);
                }
                holder.setText(item.text);
            }
        });
        builder.create().show();
    }

}