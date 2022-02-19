package com.Umentor.UmentorprojectforProgrammingIII.entity;

import com.Umentor.UmentorprojectforProgrammingIII.model.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Pattern(regexp = "^[a-zA-z \\-.]{4,20}$", message=
            "Username can only contains letters (uppercase and/or lowercase). And must be between 4-20 characters.")
    private String username;

    @Email
    private String email;

    private String password;

    @Transient
    private String passwordRepeat;

    private String avatarFilePath;

    private Boolean enabled = false;

    private String title;

    private String description;

    @ManyToMany(mappedBy = "studentList")
    private List<Course> courseList = new ArrayList<>();

    @Transient
    private int coursenum;

    public int getCoursenum() {
        return coursenum;
    }

    public void setCoursenum(int coursenum) {
        this.coursenum = coursenum;
    }


    public User(Long id, UserRole role, String username, String email, String password, String passwordRepeat, String avatarFilePath) {
        this.id = id;
        this.role = role;
        this.username = username;
        this.email = email;
        this.password = password;
        this.passwordRepeat = passwordRepeat;
        this.avatarFilePath = avatarFilePath;
    }

    public User(UserRole role, String username, String email, String password) {
        this.role = role;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(Long id, UserRole role, String username, String email, String password, String passwordRepeat, String avatarFilePath, Boolean enabled, String title, String description) {
        this.id = id;
        this.role = role;
        this.username = username;
        this.email = email;
        this.password = password;
        this.passwordRepeat = passwordRepeat;
        this.avatarFilePath = avatarFilePath;
        this.enabled = enabled;
        this.title = title;
        this.description = description;
    }

    public User() {

    }

    public Boolean getEnabled() {
        return enabled;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getAvatarFilePath() {
        return avatarFilePath;
    }

    public void setAvatarFilePath(String avatarFilePath) {
        this.avatarFilePath = avatarFilePath;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return username;
    }
    public void setName(String name)  {
        username = name;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void addCourse(Course course) {
        courseList.add(course);
        course.getStudentList().add(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role, username, email, password, avatarFilePath);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.grantedAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", role=" + role +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", avatarFilePath='" + avatarFilePath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && role == user.role && Objects.equals(username, user.username) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(avatarFilePath, user.avatarFilePath);
    }

}
