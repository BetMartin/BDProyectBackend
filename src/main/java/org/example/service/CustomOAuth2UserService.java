package org.example.service;


import org.example.entity.Person;
import org.example.entity.User;
import org.example.repository.PersonRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PersonRepository personRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByUsername(email);
        if (user == null) {
            user = new User();
            user.setUsername(email);
            user.setPassword(""); // Vacío porque autenticación es por Google

            user = userRepository.save(user);

            Person person = new Person();
            person.setFirstName(name);
            person.setLastName("Google");
            person.setUser(user);
            personRepository.save(person);
        }

        return oAuth2User;
    }
}

