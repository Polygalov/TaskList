package ua.com.adr.android.tasklist.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import ua.com.adr.android.tasklist.R;
import ua.com.adr.android.tasklist.objects.TodoDocument;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TodoList extends Activity {

	public static String TODO_DOCUMENT = "ru.javabegin.training.android.TodoDocument";
	public static int TODO_DETAILS_REQUEST = 1;

	private ListView listTasks;

	private ArrayAdapter<TodoDocument> arrayAdapter;

	private static ArrayList<TodoDocument> listDocuments = new ArrayList<TodoDocument>();

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list);

		listTasks = (ListView) findViewById(R.id.listTasks);

		listTasks.setOnItemClickListener(new ListViewClickListener());

		listTasks.setEmptyView(findViewById(R.id.emptyView));

		getActionBar().setDisplayHomeAsUpEnabled(false);

		fillTodoList();

	}

	private void fillTodoList() {
		arrayAdapter = new ArrayAdapter<TodoDocument>(this, R.layout.listview_row, listDocuments);
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
			case R.id.new_task: {
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

			TodoDocument todoDocument = null;
			switch (resultCode) {
				case RESULT_CANCELED:

					break;

				case TodoDetails.RESULT_SAVE:
					todoDocument = (TodoDocument) data
							.getSerializableExtra(TODO_DOCUMENT);
					todoDocument.setCreateDate(new Date());
					addDocument(todoDocument);
					break;

				case TodoDetails.RESULT_DELETE:
					todoDocument = (TodoDocument) data
							.getSerializableExtra(TODO_DOCUMENT);
					deleteDocument((TodoDocument) data
							.getSerializableExtra(TODO_DOCUMENT));
					break;

				default:
					break;
			}
		}
	}


	@SuppressLint("NewApi")
	private void addDocument(TodoDocument todoDocument) {

		if (todoDocument.getNumber()==null){// если документ новый, только создается
			listDocuments.add(todoDocument);
		}else{// если это старый, ранее уже созданный документ
			listDocuments.set(todoDocument.getNumber(), todoDocument);
		}

		Collections.sort(listDocuments);
		arrayAdapter.notifyDataSetChanged();

	}

	@SuppressLint("NewApi")
	private void deleteDocument(TodoDocument todoDocument) {
		listDocuments.remove(todoDocument.getNumber().intValue());
		arrayAdapter.notifyDataSetChanged();
	}

	class ListViewClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			TodoDocument todoDocument = (TodoDocument) parent.getAdapter().getItem(position);
			todoDocument.setNumber(position);
			showDocument(todoDocument);
		}

	}

}