package br.com.beautystyle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import br.com.beautystyle.retrofit.model.form.UserLoginForm;
import br.com.beautystyle.retrofit.model.dto.UserDto;
import br.com.beautystyle.model.entity.User;
import br.com.beautystyle.repository.Resource;
import br.com.beautystyle.repository.UserRepository;

public class UserViewModel extends ViewModel {

    private final UserRepository repository;

    public UserViewModel(UserRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<User>> getByEmail(String email) {
        return repository.getByEmail(email);
    }

    public LiveData<Resource<UserDto>> authUser(UserLoginForm userLoginForm) {
        return repository.authUser(userLoginForm);
    }

}
