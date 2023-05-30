package com.example.news;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.lang.UProperty;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.firebase.DatabaseFirebase;
import com.example.news.model.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {
    ListView lv_comment;
    String link, idUser;
    ArrayList<Comment> listComment;

    EditText et_comment;
    View back, submit;

    DatabaseFirebase database;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        database = new DatabaseFirebase();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        link = bundle.getString("linknews");
        idUser = bundle.getString("idUser");
        listComment = new ArrayList<Comment>();
        lv_comment = findViewById(R.id.lv_news);

        et_comment = findViewById(R.id.et_comment);
        submit = findViewById(R.id.comment);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!et_comment.getText().toString().equals("")){
                    database.addComment(idUser,link, et_comment.getText().toString());
                    UpdateLV();
                }
            }
        });
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        UpdateLV();
    }
    public void loadAllComment() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("comments").whereEqualTo("link", link)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Comment comment;
                        if (task.isSuccessful()) {
                            ArrayList<Comment> list = new ArrayList<Comment>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                comment = new Comment(document.getData().get("content").toString(), document.getData().get("idUser").toString(), document.getData().get("link").toString(), document.getId());
                                list.add(comment);
                                Log.w(TAG, comment.getId()+"", task.getException());
                            }
                            UpdateLV(list);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void UpdateLV() {
        listComment = new ArrayList<Comment>();
        loadAllComment();
    }

    public void UpdateLV(ArrayList<Comment> listRss) {
        listComment = (ArrayList<Comment>) listRss;
        ListComment listdata = new ListComment(listComment);
        lv_comment.setAdapter(listdata);
    }
    public class ListComment extends BaseAdapter {
        final ArrayList<Comment> list;

        public ListComment(ArrayList<Comment> list) {
            this.list = list;
        }
        public void displayName(String id, TextView username){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(id);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        username.setText(documentSnapshot.getString("username"));
                    } else {

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View viewString;
            if (view == null) {
                viewString = View.inflate(viewGroup.getContext(), R.layout.item_comment, null);
            } else viewString = view;
            Comment com = (Comment) getItem(i);
            TextView name = viewString.findViewById(R.id.username);
            displayName(com.getIdUser(), name);
            TextView content = viewString.findViewById(R.id.content);
            content.setText(com.getContent());
            View delete = viewString.findViewById(R.id.delete);
            if(com.getIdUser().equals(idUser)){
                delete.setVisibility(View.VISIBLE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        database.deleteComment(com.getId());
                        UpdateLV();
                    }
                });
            }else{
                delete.setVisibility(View.GONE);
            }

            return viewString;
        }
    }
}