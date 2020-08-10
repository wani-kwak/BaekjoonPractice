package com.neominum.helpme;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class InputActivity extends AppCompatActivity implements PermissionListener {

	private boolean m_isModification = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input);
		getSupportActionBar().hide();

		m_isModification = getIntent().getBooleanExtra("isModification",false);

		if (m_isModification) {
			findViewById(R.id.activity_input_cover).setVisibility(View.GONE);
			findViewById(R.id.activity_input_rl_agree).setVisibility(View.GONE);
			findViewById(R.id.activity_input_rl_description).setVisibility(View.GONE);

			final SharedPreferences sharedPreferences = getSharedPreferences("information", Context.MODE_PRIVATE);

			final String json = sharedPreferences.getString("personalMap", null);

			final HashMap<String, String> personalMap = new Gson()
				.fromJson(json, new TypeToken<HashMap<String, String>>() { }.getType());

//			personalMap.put("age", age);
//			personalMap.put("year", year);
//			personalMap.put("month", month);
//			personalMap.put("day", day);
//
//			personalMap.put("blood", blood);
//			personalMap.put("rh", rh);
//
//			personalMap.put("address", address);
//			personalMap.put("detail", detail);
//
//			personalMap.put("hospital", hospital);
//			personalMap.put("doctor", doctor);
//			personalMap.put("ment", ment);

			((AppCompatEditText) findViewById(R.id.activity_input_acet_name)).setText(personalMap.get("name"));

			((AppCompatEditText) findViewById(R.id.activity_input_acet_relationship)).setText(personalMap.get("relationship"));
			((AppCompatEditText) findViewById(R.id.activity_input_acet_phone)).setText(personalMap.get("phone"));
			((AppCompatEditText) findViewById(R.id.activity_input_acet_disease)).setText(personalMap.get("disease"));
			((AppCompatEditText) findViewById(R.id.activity_input_acet_drug)).setText(personalMap.get("drug"));

			initializeSpinnerByValue(((Spinner) findViewById(R.id.activity_input_spn_age)), personalMap.get("age"));

			initializeSpinnerByValue(((Spinner) findViewById(R.id.activity_input_spn_year)), personalMap.get("year"));
			initializeSpinnerByValue(((Spinner) findViewById(R.id.activity_input_spn_month)), personalMap.get("month"));
			initializeSpinnerByValue(((Spinner) findViewById(R.id.activity_input_spn_day)), personalMap.get("day"));

			initializeSpinnerByValue(((Spinner) findViewById(R.id.activity_input_spn_blood)), personalMap.get("blood"));
			initializeSpinnerByValue(((Spinner) findViewById(R.id.activity_input_spn_rh)), personalMap.get("rh"));

			((AppCompatTextView) findViewById(R.id.activity_input_actv_address)).setText(personalMap.get("address"));
			((AppCompatEditText) findViewById(R.id.activity_input_acet_detail)).setText(personalMap.get("detail"));

		/*
			else
		 */
			((AppCompatEditText) findViewById(R.id.activity_input_acet_hospital)).setText(personalMap.get("hospital"));
			((AppCompatEditText) findViewById(R.id.activity_input_acet_doctor)).setText(personalMap.get("doctor"));
			((AppCompatEditText) findViewById(R.id.activity_input_acet_ment)).setText(personalMap.get("ment"));
		} else {
			TedPermission.with(this)
				.setPermissionListener(this)
				.setDeniedMessage("메시지 전송 / 위치 정보를 위하여,\n아래 설정을 눌러 권한 허용 하세요")
				.setPermissions(Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE)
				.check();

			((Spinner) findViewById(R.id.activity_input_spn_age)).setSelection(41, true);
			((Spinner) findViewById(R.id.activity_input_spn_year)).setSelection(100, true);
		}
	}

	private void initializeSpinnerByValue(final Spinner spinner, final String target) {
		for (int index = 0 ; index < spinner.getCount() ; ++index) {
			if (spinner.getItemAtPosition(index).equals(target)) {
				spinner.setSelection(index, true);
				return;
			}
		}
	}

	@Override
	public void onPermissionGranted() {
		postOnCreate();
	}

	@Override
	public void onPermissionDenied(ArrayList<String> deniedPermissions) {
		finish();
	}

	public void onDescription(View view) {
		final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.activity_input_rl_description), View.ALPHA, 1.0f, 0.0f);

		objectAnimator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				findViewById(R.id.activity_input_rl_description).setVisibility(View.GONE);
			}
		});

		objectAnimator.setDuration(1000);
		objectAnimator.start();
	}

	public void onSearch(View view) {
		final String query = ((AppCompatEditText) findViewById(R.id.activity_input_acet_address)).getText().toString().trim();

		if (query.equals("")) {
			Toast.makeText(this, "우편번호를 입력 후 검색하세요", Toast.LENGTH_LONG).show();
		} else {
			final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

			new AsyncTask<Void, Void, Document>() {

				@Override
				protected Document doInBackground(Void... voids) {

					try {
						final Document document = Jsoup
							.connect("https://biz.epost.go.kr/KpostPortal/openapi")
							.data("regkey", "9f1635c8d881b80001526266169")
							.data("target", "postNew")
							.data("query", query)
							.data("countPerPage", "50")
							.post();

//						Log.d("NEOMINUM", document.toString());

						return document;
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				}

				@Override
				protected void onPostExecute(Document document) {
					try {
						if (document == null) {
							Toast.makeText(InputActivity.this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show();
						} else {
							final String totalCount = document.select("post > pageinfo > totalCount").text().trim();
							if (totalCount.equals("") || "0".equals(totalCount)) {
								Toast.makeText(InputActivity.this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show();
							} else {
								final ArrayList<String> addresses = new ArrayList<>();
								final Elements elements = document.select("post > itemlist > item");
								for (final Element element : elements) {
//									Log.d("NEOMINUM", element.select("item > addrjibun").text());
									addresses.add(element.select("item > addrjibun").text());
								}
								AlertDialog.Builder builder = new AlertDialog.Builder(InputActivity.this);
								builder
									.setTitle("주소 선택")
									.setItems(
										addresses.toArray(new CharSequence[addresses.size()]),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialogInterface, int index) {
												((AppCompatTextView) InputActivity.this.findViewById(R.id.activity_input_actv_address)).setText(addresses.get(index));
											}
										})
									.show();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(InputActivity.this, "검색 실패", Toast.LENGTH_SHORT).show();
					}
				}
			}.execute();
		}
	}

	private void postOnCreate() {
		final SharedPreferences sharedPreferences = getSharedPreferences("information", Context.MODE_PRIVATE);

		if (sharedPreferences.contains("personalMap")) {
			startActivity(new Intent(this, MainActivity.class));
		} else {
			findViewById(R.id.activity_input_cover).setVisibility(View.GONE);
		}
	}

	public void onCancel(View view) {
		finish();
	}

	public void onDocument(View view) {
		new AlertDialog.Builder(this)
			.setView(R.layout.dialog_document)
			.show();
	}

	public void onAgree(View view) {
		if (((AppCompatCheckBox) findViewById(R.id.activity_input_accb_agree)).isChecked()) {
			((AppCompatButton) findViewById(R.id.activity_input_acbtn_agree)).setEnabled(false);

			final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.activity_input_rl_agree), View.ALPHA, 1.0f, 0.0f);

			objectAnimator.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					findViewById(R.id.activity_input_rl_agree).setVisibility(View.GONE);
				}
			});

			objectAnimator.setDuration(1000);
			objectAnimator.start();
		} else {
			Toast.makeText(this, "이용약관에 동의 해주세요", Toast.LENGTH_LONG).show();
		}
	}

	public void onRegister(View view) {
		/*
			essential
		 */
		final String name = ((AppCompatEditText) findViewById(R.id.activity_input_acet_name)).getText().toString().trim();

		final String relationship = ((AppCompatEditText) findViewById(R.id.activity_input_acet_relationship)).getText().toString().trim();
		final String phone = ((AppCompatEditText) findViewById(R.id.activity_input_acet_phone)).getText().toString().trim();
		final String disease = ((AppCompatEditText) findViewById(R.id.activity_input_acet_disease)).getText().toString().trim();
		final String drug = ((AppCompatEditText) findViewById(R.id.activity_input_acet_drug)).getText().toString().trim();

		final String age = ((Spinner) findViewById(R.id.activity_input_spn_age)).getSelectedItem().toString().trim();

		final String year = ((Spinner) findViewById(R.id.activity_input_spn_year)).getSelectedItem().toString().trim();
		final String month = ((Spinner) findViewById(R.id.activity_input_spn_month)).getSelectedItem().toString().trim();
		final String day = ((Spinner) findViewById(R.id.activity_input_spn_day)).getSelectedItem().toString().trim();

		final String blood = ((Spinner) findViewById(R.id.activity_input_spn_blood)).getSelectedItem().toString().trim();
		final String rh = ((Spinner) findViewById(R.id.activity_input_spn_rh)).getSelectedItem().toString().trim();

		final String address = ((AppCompatTextView) findViewById(R.id.activity_input_actv_address)).getText().toString().trim();
		final String detail = ((AppCompatEditText) findViewById(R.id.activity_input_acet_detail)).getText().toString().trim();

		/*
			else
		 */
		final String hospital = ((AppCompatEditText) findViewById(R.id.activity_input_acet_hospital)).getText().toString().trim();
		final String doctor = ((AppCompatEditText) findViewById(R.id.activity_input_acet_doctor)).getText().toString().trim();
		final String ment = ((AppCompatEditText) findViewById(R.id.activity_input_acet_ment)).getText().toString().trim();

		if (name.equals("") ||
			relationship.equals("") ||
			phone.equals("") ||
			disease.equals("") ||
			drug.equals("") ||
			age.equals("") ||
			year.equals("") ||
			month.equals("") ||
			day.equals("") ||
			blood.equals("") ||
			rh.equals("") ||
			address.equals("")) {

			Toast.makeText(this, "필수 작성 항목을 모두 채워주세요", Toast.LENGTH_LONG).show();
		} else {
			final SharedPreferences sharedPreferences = getSharedPreferences("information", Context.MODE_PRIVATE);

			final SharedPreferences.Editor editor = sharedPreferences.edit();

			final HashMap<String, String> personalMap = new HashMap<>();

			personalMap.put("name", name);
			personalMap.put("relationship", relationship);
			personalMap.put("phone", phone);
			personalMap.put("disease", disease);
			personalMap.put("drug", drug);

			personalMap.put("age", age);
			personalMap.put("year", year);
			personalMap.put("month", month);
			personalMap.put("day", day);

			personalMap.put("blood", blood);
			personalMap.put("rh", rh);

			personalMap.put("address", address);
			personalMap.put("detail", detail);

			personalMap.put("hospital", hospital);
			personalMap.put("doctor", doctor);
			personalMap.put("ment", ment);

			final String json = new Gson().toJson(personalMap);

			editor.putString("personalMap", json);

			editor.commit();

//			finish();
			startActivity(new Intent(this, MainActivity.class));
		}
	}
}
