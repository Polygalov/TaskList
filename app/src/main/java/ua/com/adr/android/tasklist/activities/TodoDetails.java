package ua.com.adr.android.tasklist.activities;

import ua.com.adr.android.tasklist.R;
import ua.com.adr.android.tasklist.objects.TodoDocument;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class TodoDetails extends Activity {

	public static final int RESULT_SAVE = 100;
	public static final int RESULT_DELETE = 101;

	private EditText txtTodoDetails;
	private TodoDocument todoDocument;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_details);
		
		txtTodoDetails = (EditText) findViewById(R.id.txtTodoDetails);
		todoDocument = (TodoDocument) getIntent().getSerializableExtra(TodoList.TODO_DOCUMENT);
		// setTitle(todoDocument.getName());
		txtTodoDetails.setText(todoDocument.getContent());

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:{
		
			setResult(RESULT_CANCELED);
			finish();
			return true;
		}
		
		case R.id.save:{
			setResult(RESULT_SAVE);
			finish();
			return true;
		}

		default:
			break;
		}
		
		
		return super.onOptionsItemSelected(item);
	}
}
