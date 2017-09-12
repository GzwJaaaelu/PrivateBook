package com.jaaaelu.gzw.neteasy.privatebook.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.jaaaelu.gzw.neteasy.common.app.BaseActivity;
import com.jaaaelu.gzw.neteasy.evernote.task.CreateNewNoteTask;
import com.jaaaelu.gzw.neteasy.evernote.task.FindNotesTask;
import com.jaaaelu.gzw.neteasy.evernote.task.GetNoteHtmlTask;
import com.jaaaelu.gzw.neteasy.privatebook.R;
import com.jaaaelu.gzw.neteasy.privatebook.fragments.everNote.CreateNoteDialogFragment;

import net.vrallev.android.task.TaskResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class EverNoteActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final int MAX_NOTES = 20;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_note_list)
    ListView mNoteList;
    @BindView(R.id.srl_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.fab_add_note)
    FloatingActionButton mFabAddNote;
    private List<NoteRef> mNoteRefList = new ArrayList<>();
    private LinkedNotebook mLinkedNotebook;
    private MyAdapter mAdapter;
    private Notebook mNotebook;

    /**
     * 跳转到当前 Activity
     *
     * @param context
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, EverNoteActivity.class));
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ever_note;
    }

    @Override
    protected void initView() {
        super.initView();
        initToolbar();

        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mRefreshLayout.setOnRefreshListener(this);

        mAdapter = new MyAdapter();
        mNoteList.setAdapter(mAdapter);
        mNoteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new GetNoteHtmlTask(mNoteRefList.get(position)).start(EverNoteActivity.this, "html");
            }
        });

        mFabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreateNoteDialogFragment().show(getSupportFragmentManager(), CreateNoteDialogFragment.TAG);
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        if (!EvernoteSession.getInstance().isLoggedIn()) {
            return;
        }
        queryEverNoteBook();
    }

    private void queryEverNoteBook() {
        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        noteStoreClient.listNotebooksAsync(new EvernoteCallback<List<Notebook>>() {
            @Override
            public void onSuccess(List<Notebook> result) {
                for (Notebook notebook : result) {
                    if ("私人藏书".equals(notebook.getName())) {
                        mNotebook = notebook;
                        queryEverNote();
                    }
                }
            }

            @Override
            public void onException(Exception exception) {

            }
        });
    }

    private void queryEverNote() {
        new FindNotesTask(0, MAX_NOTES, mNotebook, mLinkedNotebook, "").start(EverNoteActivity.this);
    }

    @TaskResult
    public void onFindNotes(List<NoteRef> noteRefList) {
        mRefreshLayout.setRefreshing(false);
        if (noteRefList == null || noteRefList.isEmpty()) {
            Toast.makeText(getApplicationContext(), "好像没有数据返回", Toast.LENGTH_LONG).show();
        } else {
            mNoteRefList.clear();
            mNoteRefList.addAll(noteRefList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @TaskResult
    public void onCreateNewNote(Note note) {
        if (note != null) {
            onRefresh();
        }
    }

    @TaskResult(id = "html")
    public void onGetNoteContentHtml(String html, GetNoteHtmlTask task) {
        WebViewActivity.show(this, task.getNoteRef(), html);
    }

    public void createNewNote(String title, String content, CreateNewNoteTask.ImageData imageData) {
        new CreateNewNoteTask(title, content, imageData, mNotebook, mLinkedNotebook).start(this);
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setRefreshing(true);
        queryEverNote();
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNoteRefList.size();
        }

        @Override
        public NoteRef getItem(int position) {
            return mNoteRefList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            NoteRef noteRef = getItem(position);
            viewHolder.mTextView1.setText(noteRef.getTitle());

            return convertView;
        }
    }

    private static class ViewHolder {

        private final TextView mTextView1;

        ViewHolder(View view) {
            mTextView1 = (TextView) view.findViewById(android.R.id.text1);
        }
    }
}
