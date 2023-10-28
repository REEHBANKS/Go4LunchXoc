package com.metanoiasystem.go4lunchxoc.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlarmManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.metanoiasystem.go4lunchxoc.R;
import com.metanoiasystem.go4lunchxoc.data.models.Restaurant;
import com.metanoiasystem.go4lunchxoc.data.models.RestaurantWithNumberUser;
import com.metanoiasystem.go4lunchxoc.data.models.UserAndPictureWithYourSelectedRestaurant;
import com.metanoiasystem.go4lunchxoc.databinding.ActivityRestaurantDetailBinding;
import com.metanoiasystem.go4lunchxoc.domain.usecase.AddToFavoritesUseCase;
import com.metanoiasystem.go4lunchxoc.domain.usecase.CheckIfRestaurantSelectedUseCase;
import com.metanoiasystem.go4lunchxoc.domain.usecase.CreateNewSelectedRestaurantUseCase;
import com.metanoiasystem.go4lunchxoc.domain.usecase.FetchAllUsersUseCase;
import com.metanoiasystem.go4lunchxoc.domain.usecase.GetSelectedRestaurantsWithIdUseCase;
import com.metanoiasystem.go4lunchxoc.domain.usecase.GetUserChosenRestaurantsUseCase;
import com.metanoiasystem.go4lunchxoc.domain.usecase.UpdateExistingRestaurantSelectionUseCaseImpl;
import com.metanoiasystem.go4lunchxoc.utils.AlarmHelper;
import com.metanoiasystem.go4lunchxoc.utils.CheckAndHandleExistingRestaurantSelectionUseCase;
import com.metanoiasystem.go4lunchxoc.utils.GetCurrentDateUseCase;
import com.metanoiasystem.go4lunchxoc.utils.GetCurrentUseCase;
import com.metanoiasystem.go4lunchxoc.utils.HandleExistingSelectionUseCase;
import com.metanoiasystem.go4lunchxoc.utils.ImageUtils;
import com.metanoiasystem.go4lunchxoc.utils.Injector;
import com.metanoiasystem.go4lunchxoc.utils.PreferencesManager;
import com.metanoiasystem.go4lunchxoc.utils.RatingUtils;
import com.metanoiasystem.go4lunchxoc.utils.callbacks.UseCaseCallback;
import com.metanoiasystem.go4lunchxoc.view.fragments.RestaurantSelectorListFragment;
import com.metanoiasystem.go4lunchxoc.viewmodels.RestaurantDetailViewModel;

import java.util.List;


public class RestaurantDetailActivity extends AppCompatActivity {

    public static String RESTAURANT_KEY = "RESTAURANT_KEY";
    private RestaurantWithNumberUser restaurantWithNumberUser;
    private ActivityRestaurantDetailBinding binding;
    private RestaurantDetailViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        viewModel = new ViewModelProvider(this).get(RestaurantDetailViewModel.class);

        // Récupération de l'objet Restaurant passé en extra
        restaurantWithNumberUser = (RestaurantWithNumberUser) getIntent().getSerializableExtra(RESTAURANT_KEY);

        if (restaurantWithNumberUser != null) {

            addRestaurantSelectorListFragment();
        }



        observeViewModel();
        setPicture();
        setNameAndAddress(restaurantWithNumberUser.getRestaurant());
        setRating();
        UpdateButton();
    }

    private void addRestaurantSelectorListFragment(){
        // Passez l'objet Restaurant au Fragment

        Bundle bundle = new Bundle();
        bundle.putSerializable(RESTAURANT_KEY, restaurantWithNumberUser);

        // Trouvez le fragment par son ID ou créez une nouvelle instance et ajoutez-le à l'activité
        RestaurantSelectorListFragment fragment = (RestaurantSelectorListFragment) getSupportFragmentManager().findFragmentById(R.id.containerDetail);
        if (fragment != null) {
            fragment.setArguments(bundle);
        } else {
            fragment = new RestaurantSelectorListFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.containerDetail, fragment).commit();
        }

    }

    // Set Image

    public void setPicture(){
        ImageUtils.loadRestaurantImage(binding.pictureRestaurantDetail, restaurantWithNumberUser.getRestaurant());
    }



    private void setNameAndAddress(Restaurant restaurant) {
        binding.nameRestaurantDetail.setText(restaurant.getRestaurantName());
        binding.addressRestaurantDetail.setText(restaurant.getRestaurantAddress());
    }

    public void setRating() {
        RatingUtils.setRating(binding.itemListRestaurantRatingBar, restaurantWithNumberUser.getRestaurant().getRating());
    }

    public void UpdateButton() {

        binding.buttonLikeRestaurantDetail.setOnClickListener(view -> {
            if (restaurantWithNumberUser != null) {
                viewModel.createNewRestaurantFavorites(restaurantWithNumberUser.getRestaurant().getId());
            }
        });

        binding.buttonSelectedRestaurant.setOnClickListener(view -> {
            if (restaurantWithNumberUser != null) {
                viewModel.createOrUpdateSelectedRestaurant(restaurantWithNumberUser.getRestaurant().getId());

                PreferencesManager preferencesManager = new PreferencesManager(this);
                preferencesManager.saveRestaurantInfo(restaurantWithNumberUser.getRestaurant().getRestaurantName(),
                        restaurantWithNumberUser.getRestaurant().getRestaurantAddress());


            }
        });

        if (restaurantWithNumberUser.getRestaurant().getNumberPhone() != null) {
            binding.buttonCallRestaurantDetail.setOnClickListener(v -> Toast.makeText(getApplicationContext()
                    , restaurantWithNumberUser.getRestaurant().getNumberPhone(), Toast.LENGTH_SHORT).show());
        } else {
            binding.buttonCallRestaurantDetail.setOnClickListener(v -> Toast.makeText(getApplicationContext()
                    , getString(R.string.unavailable_number), Toast.LENGTH_SHORT).show());
        }

        if (restaurantWithNumberUser.getRestaurant().getEmail() != null) {
            binding.buttonWebsiteRestaurantDetail.setOnClickListener(v -> Toast.makeText(getApplicationContext()
                    , restaurantWithNumberUser.getRestaurant().getEmail(), Toast.LENGTH_SHORT).show());
        } else {
            binding.buttonWebsiteRestaurantDetail.setOnClickListener(v -> Toast.makeText(getApplicationContext()
                    , getString(R.string.unavailable_website), Toast.LENGTH_SHORT).show());
        }

    }

    private void observeViewModel() {
        viewModel.getAddSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, getString(R.string.restaurant_added_favorites_success), Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.errorMessage.observe(this, errorMsg -> {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
        });

        viewModel.isRestaurantCreated().observe(this, success -> {
            if (success) {
                Toast.makeText(this, getString(R.string.restaurant_added_selection_success), Toast.LENGTH_SHORT).show();

                // Récupérez les données de SharedPreferences
                PreferencesManager preferencesManager = new PreferencesManager(this);
                String restaurantName = preferencesManager.getRestaurantName();
                String restaurantAddress = preferencesManager.getRestaurantAddress();

                if (preferencesManager.areNotificationsEnabled()) {
                    AlarmHelper alarmHelper = new AlarmHelper(this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmHelper.configureAlarm(restaurantName, restaurantAddress);
                    }
                }


            }
        });

        viewModel.isRestaurantUpdated().observe (this, success -> {
            if (success) {
                Toast.makeText(this,  getString(R.string.restaurant_selection_modified), Toast.LENGTH_SHORT).show();
            }
        });
    }

}