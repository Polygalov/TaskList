package ua.com.adr.android.tasklist.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import ua.com.adr.android.tasklist.R;
import ua.com.adr.android.tasklist.activities.utils.ImageUtils;
import ua.com.adr.android.tasklist.enums.PriorityType;
import ua.com.adr.android.tasklist.objects.AppContext;
import ua.com.adr.android.tasklist.objects.TodoDocument;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class TodoDetails extends Activity {

	public static final String IMAGE_PATH = "ru.javabegin.training.android.todoproject.activities.TodoDetails.ImagePath";

	private static final int CAPTURE_IMAGE_REQUEST = 100;

	public static final int RESULT_SAVE = 100;
	public static final int RESULT_DELETE = 101;

	private static final int NAME_LENGTH = 20;

	private EditText txtTodoDetails;
	private ImageView imgTodo;
	private FrameLayout frameImage;

	private String imagePath;

	private TodoDocument todoDocument;

	private ArrayList<TodoDocument> listDocuments;

	private int actionType;
	private int docIndex;

	private MenuItem menuPriority;
	private PriorityType currentPriorityType;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		listDocuments = ((AppContext) getApplicationContext())
				.getListDocuments();

		actionType = getIntent().getExtras().getInt(AppContext.ACTION_TYPE);

		setContentView(R.layout.activity_todo_details);

		txtTodoDetails = (EditText) findViewById(R.id.txtTodoDetails);
		frameImage = (FrameLayout) findViewById(R.id.frameImage);

		prepareDocument(actionType);

		imgTodo = (ImageView) findViewById(R.id.imgTodo);
		imgTodo.setOnClickListener(new ImageClickListener());

		if (notEmpty(todoDocument.getImagePath())) {
			frameImage.setVisibility(View.VISIBLE);
			attachPhoto(todoDocument.getImagePath());
		} else {
			frameImage.setVisibility(View.GONE);
		}

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	private void prepareDocument(int actionType) {
		switch (actionType) {
		case AppContext.ACTION_NEW_TASK:
			todoDocument = new TodoDocument();
			todoDocument.setCreateDate(new Date());
			break;

		case AppContext.ACTION_UPDATE:
			docIndex = getIntent().getExtras().getInt(AppContext.DOC_INDEX);
			todoDocument = listDocuments.get(docIndex);
			txtTodoDetails.setText(todoDocument.getContent());
			break;

		default:
			break;
		}

		currentPriorityType = todoDocument.getPriorityType();
	}

	public void deleteImage(View view) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.confirm_delete_image);

		builder.setPositiveButton(R.string.delete,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (notEmpty(imagePath)){
							deleteImageFile(imagePath);
						}
						frameImage.setVisibility(View.GONE);
						imagePath = null;
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();

	}

	private void saveDocument() {

		if (actionType == AppContext.ACTION_UPDATE) {

			boolean edited = false;

			SharedPreferences sharedPref = getSharedPreferences(
					String.valueOf(todoDocument.getCreateDate().getTime()),
					Context.MODE_PRIVATE);

			Editor editor = sharedPref.edit();

			// ���� �������� ������ � ����� ���������
			if (!txtTodoDetails.getText().toString().trim()
					.equals(todoDocument.getContent())) {

				todoDocument.setName(getDocumentName());
				todoDocument.setContent(txtTodoDetails.getText().toString()
						.trim());
				editor.putString(AppContext.FIELD_CONTENT,
						todoDocument.getContent());
				edited = true;
			}

			// ���� ��������� ���������
			if (currentPriorityType != todoDocument.getPriorityType()) {
				todoDocument.setPriorityType(currentPriorityType);
				editor.putInt(AppContext.FIELD_PRIORITY_TYPE, todoDocument
						.getPriorityType().getIndex());
				edited = true;
			}

			// ���� ��������/��������/������� �����������
			if ((notEmpty(imagePath) && !imagePath.equals(todoDocument.getImagePath())) ||
					(notEmpty(todoDocument.getImagePath()) && !todoDocument.getImagePath().equals(imagePath))
					
					) {
				
				if (notEmpty(todoDocument.getImagePath())) {// ������� ������ �����������
					deleteImageFile(todoDocument.getImagePath());
				}

				todoDocument.setImagePath(imagePath);
				edited = true;
			}

			if (edited) {
				String path = ((AppContext) getApplicationContext())
						.getPrefsDir();
				File file = new File(path, todoDocument.getCreateDate()
						.getTime() + ".xml");

				todoDocument.setCreateDate(new Date());
				editor.putString(AppContext.FIELD_NAME, todoDocument.getName());
				editor.putLong(AppContext.FIELD_CREATE_DATE, todoDocument
						.getCreateDate().getTime());
				editor.putString(AppContext.FIELD_IMAGE_PATH,
						todoDocument.getImagePath());
				editor.commit();

				file.renameTo(new File(path, todoDocument.getCreateDate()
						.getTime() + ".xml"));

			}

		} else if (actionType == AppContext.ACTION_NEW_TASK) {
			todoDocument.setName(getDocumentName());
			todoDocument.setCreateDate(new Date());
			todoDocument.setContent(txtTodoDetails.getText().toString().trim());
			todoDocument.setPriorityType(currentPriorityType);

			if (imagePath != null)
				todoDocument.setImagePath(imagePath);

			SharedPreferences sharedPref = getSharedPreferences(
					String.valueOf(todoDocument.getCreateDate().getTime()),
					Context.MODE_PRIVATE);
			Editor editor = sharedPref.edit();
			editor.putString(AppContext.FIELD_CONTENT,
					todoDocument.getContent());
			editor.putString(AppContext.FIELD_NAME, todoDocument.getName());
			editor.putLong(AppContext.FIELD_CREATE_DATE, todoDocument
					.getCreateDate().getTime());
			editor.putInt(AppContext.FIELD_PRIORITY_TYPE, todoDocument
					.getPriorityType().getIndex());
			editor.putString(AppContext.FIELD_IMAGE_PATH,
					todoDocument.getImagePath());

			editor.commit();

			listDocuments.add(todoDocument);

		}

		finish();

	}

	private String getDocumentName() {
		StringBuilder sb = new StringBuilder(txtTodoDetails.getText());

		if (sb.length() > NAME_LENGTH) {
			sb.delete(NAME_LENGTH, sb.length()).append("...");
		}

		String tmpName = sb.toString().trim().split("\n")[0];

		String name = (tmpName.length() > 0) ? tmpName : getResources()
				.getString(R.string.new_document);

		return name;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_details, menu);

		menuPriority = menu.findItem(R.id.menu_priority);

		MenuItem menuItem = menuPriority.getSubMenu().getItem(
				todoDocument.getPriorityType().getIndex());
		menuItem.setChecked(true);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {

			saveDocument();

			return true;
		}

		case R.id.save: {

			saveDocument();

			return true;
		}

		case R.id.delete: {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.confirm_delete_todo);

			builder.setPositiveButton(R.string.delete,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							deleteDocument(todoDocument);
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {

						}
					});
			AlertDialog dialog = builder.create();
			dialog.show();

			return true;
		}

		case R.id.menu_priority_low:
		case R.id.menu_priority_middle:
		case R.id.menu_priority_high: {
			item.setChecked(true);
			currentPriorityType = PriorityType.values()[Integer.valueOf(item
					.getTitleCondensed().toString())];

			return true;
		}

		case R.id.menu_take_photo: {

			Intent intentAttachPhoto = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);

			Uri uri = Uri.fromFile(getImagePath());

			if (notEmpty(imagePath)){// ���� ������ ��������� ��� ������ - ������ ���� �������
				deleteImageFile(imagePath);
			}
			
			imagePath = uri.getPath();

			intentAttachPhoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intentAttachPhoto, CAPTURE_IMAGE_REQUEST);

			return true;
		}

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("NewApi")
	private void deleteDocument(TodoDocument todoDocument) {

		// ���� ������ ������ ��� ��������� �������� - ������ �� ������, �.�.
		// ������ � �� ���������, ����� �������
		// ���� ������ ����� ��������� ��������, ����� ������� ���
		if (actionType == AppContext.ACTION_UPDATE) {
			Intent intent = new Intent(AppContext.RECEIVER_DELETE_DOCUMENT);
			intent.putExtra(AppContext.DOC_INDEX, todoDocument.getNumber());
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		} else {// xml ���� �� ��� ������, �.�. �������� ����� - ������ �������
				// ����������, ���� ����
			if (notEmpty(imagePath)) {
				deleteImageFile(imagePath);
				imagePath = null;
			}
		}

		finish();
	}

	private boolean notEmpty(String val) {
		return val != null && !val.equals("");
	}

	private boolean deleteImageFile(String path) {
		File f = new File(path);
		if (f.exists()) {
			return f.delete();
		}
		return false;
	}
	

	private File getImagePath() {
		File directory = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				getPackageName());
		if (!directory.exists()) {
			directory.mkdirs();
		}

		return new File(directory.getPath() + File.separator
				+ UUID.randomUUID() + ".jpg");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(IMAGE_PATH, imagePath);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		imagePath = savedInstanceState.getString(IMAGE_PATH);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CAPTURE_IMAGE_REQUEST) {
			if (resultCode == RESULT_OK && notEmpty(imagePath) && new File(imagePath).exists()) {
				// imagePath = data.getData().getPath();// �� ��������, ����
				// ��������� EXTRA_OUTPUT
				attachPhoto(imagePath);
			} else {// ���� �������� ����������������
				if (todoDocument.getImagePath()!=null){
					imagePath = todoDocument.getImagePath();
				}
			}
		}
	}

	private void attachPhoto(String path) {
		frameImage.setVisibility(View.VISIBLE);
		imgTodo.setImageBitmap(ImageUtils.getSizedBitmap(path, AppContext.IMAGE_WIDTH_THMB, AppContext.IMAGE_HEIGHT_THMB));
	}

	public void openImage(View view) {
		Intent intentFullImage = new Intent(TodoDetails.this, FullImage.class);
		if (notEmpty(imagePath)) {// ���� ��������� ������ ��� ��������� �����������
			intentFullImage.putExtra(IMAGE_PATH, imagePath);
		}else{
			intentFullImage.putExtra(IMAGE_PATH, todoDocument.getImagePath());
		}
		startActivity(intentFullImage);
	}

	private class ImageClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			openImage(v);
		}

	}
}
