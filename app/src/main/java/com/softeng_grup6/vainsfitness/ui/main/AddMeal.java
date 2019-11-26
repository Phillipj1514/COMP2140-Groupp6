package com.softeng_grup6.vainsfitness.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.softeng_grup6.vainsfitness.MainActivity;
import com.softeng_grup6.vainsfitness.R;
import com.softeng_grup6.vainsfitness.listeners.CalorieHandler;
import com.softeng_grup6.vainsfitness.listeners.CalorieListener;
import com.softeng_grup6.vainsfitness.listeners.MealPlanHandler;
import com.softeng_grup6.vainsfitness.listeners.NetSessionListener;
import com.softeng_grup6.vainsfitness.managers.NetworkManager;
import com.softeng_grup6.vainsfitness.managers.UserInterfaceManager;
import com.softeng_grup6.vainsfitness.systems.AdminSystem;
import com.softeng_grup6.vainsfitness.utils.Admin;
import com.softeng_grup6.vainsfitness.utils.CalorieAPI;
import com.softeng_grup6.vainsfitness.utils.MealPlan;

import java.util.ArrayList;

public class AddMeal extends AppCompatActivity {
    private Button addFromMealPlan = null;
    private Button addButton = null;
    private Button doneButton = null;
    private TextView title = null;
    private TextView mealDisplay = null;
    private TextView descriptionTitle = null;
    private TextView mealDisplayTitle = null;
    private EditText mealDetails = null;
    private EditText mealDescription = null;
    private LinearLayout meal_description_layout = null;
    public static CalorieHandler calorieHandler = new CalorieHandler();
    public static MealPlanHandler mealPlanHandler = new MealPlanHandler();
    String meal_name = "";
    ArrayList<String> meal_items = new ArrayList<>();
    String display_text = "Meal name - ";
    int count = 0;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmeal);
        addFromMealPlan = (Button)findViewById(R.id.add_from_mealplan);
        addButton = (Button)findViewById(R.id.add);
        doneButton = (Button)findViewById(R.id.done);
        title = (TextView)findViewById(R.id.title);
        mealDisplay = (TextView)findViewById(R.id.meal_display);
        descriptionTitle = (TextView)findViewById(R.id.meal_desc_title);
        mealDisplayTitle = (TextView)findViewById(R.id.meal_display_title);
        mealDetails = (EditText) findViewById(R.id.meal_name);
        mealDescription = (EditText) findViewById(R.id.meal_meth);
        meal_description_layout = (LinearLayout)findViewById(R.id.add_desc);
        if(UserInterfaceManager.getLoggedInUserType().equals("admin")){
            adminConfiguration();
        }else if (UserInterfaceManager.getLoggedInUserType().equals("client")){
            clientConfiguration();
        }
    }
    private void adminConfiguration(){
        addFromMealPlan.setVisibility(View.GONE);
        title.setText("Add New Meal Plan");
        descriptionTitle.setText("Preparation Method");
        mealDescription.setHint("Enter the meals method of preparation");
        mealDisplayTitle.setText("Meal Plan Details\n============================");
        mealDisplay.setText(display_text);
        mealDetails.setHint("Enter Meal Plan Name then press add");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String add_text = mealDetails.getText().toString();
                if(add_text.length() > 1){
                    if(count > 0){
                        meal_items.add(add_text);
                        display_text = display_text+"\t\t"+add_text+"\n";

                    }else{
                        meal_name = add_text;
                        display_text = display_text+add_text+"\nItems -\n";
                        count++;
                        mealDetails.setHint("Enter the Items with quantity eg.(2 egg)");
                    }
                }else{
                    Toast.makeText(AddMeal.this, "Please enter some text before you submit", Toast.LENGTH_SHORT).show();
                }
                mealDetails.setText("");
                mealDisplay.setText(display_text);
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String method = mealDescription.getText().toString();
                if(method.length() > 4){
                    CalorieAPI calorieAPI = new CalorieAPI(getApplicationContext());
                    //Toast.makeText(AddMeal.this, "meal 1: "+meal_items.get(0), Toast.LENGTH_SHORT).show();
                    calorieAPI.getCalorie("addmealplan",meal_items);
                    calorieHandler.setOnCalorieFetchListener(new CalorieListener() {
                        @Override
                        public void success(int calorie_value) {
                            MealPlan mealPlan = new MealPlan(meal_name,meal_items,calorie_value,method);
//                            Toast.makeText(AddMeal.this, "Meal Plan Name "+mealPlan.getName(), Toast.LENGTH_SHORT).show();
                            AdminSystem.getAdminProfile().getAdminMealPlans().addMealPlanToList(mealPlan);
                            NetworkManager session  = new NetworkManager();
                            session.updateMealPlans(AdminSystem.getAdminProfile().getAdminMealPlans());
                            mealPlanHandler.setOnUpdateMealPlanListener(new NetSessionListener() {
                                @Override
                                public void succees() {
                                    Toast.makeText(AddMeal.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                                    returnHome();
                                }

                                @Override
                                public void unsuccessful() {
                                    Toast.makeText(AddMeal.this, "Network Problem", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                        @Override
                        public void unsuccessFul() {
                            count = 0;
                            meal_name = "";
                            mealDetails.setText("");
                            display_text = "Meal name - ";
                            meal_items.clear();
                            mealDescription.setText("");
                            mealDisplay.setText(display_text);
                           // Toast.makeText(AddMeal.this, "Network Issue", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    Toast.makeText(AddMeal.this, "Enter the method of preparation or a link", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private  void clientConfiguration(){
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnHome();
            }
        });
        addFromMealPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(getApplicationContext(), AddMealPlan.class);
                startActivity(go);
                finish();
            }
        });

    }

    private void returnHome(){
        Intent go = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(go);
        finish();
    }
}
