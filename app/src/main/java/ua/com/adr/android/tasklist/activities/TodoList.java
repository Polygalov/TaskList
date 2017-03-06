package ua.com.adr.android.tasklist.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ua.com.adr.android.tasklist.R;
import ua.com.adr.android.tasklist.objects.TodoDocument;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class TodoList extends Activity {

	public static String TODO_DOCUMENT = "ru.javabegin.training.android.TodoDocument";
	public static int TODO_DETAILS_REQUEST = 1;

	private ListView listTasks;

	private ArrayAdapter<TodoDocument> arrayAdapter;
	private static List<TodoDocument> listDocument = new ArrayList<TodoDocument>();

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list);

		listTasks = (ListView) findViewById(R.id.listTasks);

		listTasks.setOnItemClickListener(new ListViewClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(false);

		fillTodoList();

	}

	private void fillTodoList() {

		// ��������
		TodoDocument d1 = new TodoDocument("s1", "c1", null);
		TodoDocument d2 = new TodoDocument("s2", "c2", null);
		TodoDocument d3 = new TodoDocument("s3", "c3", null);

		List<TodoDocument> listDocument = new ArrayList<TodoDocument>();
		listDocument.add(d1);
		listDocument.add(d2);
		listDocument.add(d3);

		arrayAdapter = new ArrayAdapter<TodoDocument>(getApplicationContext(),
				R.layout.listview_row, listDocument);
		listTasks.setAdapter(arrayAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_task: {
			TodoDocument todoDocument = new TodoDocument();
			todoDocument.setName(getResources()
					.getString(R.string.new_document));
			showDocument(todoDocument);
			return true;
		}

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showDocument(TodoDocument todoDocument) {
		Intent intentTodoDetails = new Intent(this, TodoDetails.class);
		intentTodoDetails.putExtra(TODO_DOCUMENT, todoDocument);
		startActivityForResult(intentTodoDetails, TODO_DETAILS_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TODO_DETAILS_REQUEST) {
			switch (resultCode) {
			case RESULT_CANCELED:
				Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
				break;

			case TodoDetails.RESULT_SAVE:
				Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}
	}

	class ListViewClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			TodoDocument todoDocument = (TodoDocument) parent.getAdapter().getItem(position);
			showDocument(todoDocument);
		}

	}

}
