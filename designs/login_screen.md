# Login Screen

## Purpose
Allow users to authenticate and access the system based on their role.

## Design

```
+------------------------------------------------------+
|                                                      |
|                                                      |
|                                                      |
|        +----------------------------------+          |
|        |                                  |          |
|        |     EVENT MANAGEMENT SYSTEM      |          |
|        |                                  |          |
|        +----------------------------------+          |
|                                                      |
|        +----------------------------------+          |
|        |         Welcome Back!            |          |
|        +----------------------------------+          |
|                                                      |
|        +----------------------------------+          |
|        | Email:                           |          |
|        | [                              ] |          |
|        +----------------------------------+          |
|                                                      |
|        +----------------------------------+          |
|        | Password:                        |          |
|        | [                              ] |          |
|        +----------------------------------+          |
|                                                      |
|        +----------------------------------+          |
|        |             [ LOGIN ]            |          |
|        +----------------------------------+          |
|                                                      |
|        +----------------------------------+          |
|        | Forgot Password? | Sign Up       |          |
|        +----------------------------------+          |
|                                                      |
|                                                      |
+------------------------------------------------------+
```

## Elements

1. **Header**
   - System name in large, bold font
   - Welcoming message

2. **Login Form**
   - Email input field with validation
   - Password input field with show/hide toggle
   - Error message area (displays validation errors)
   - Remember me checkbox (optional)

3. **Action Buttons**
   - Login button (primary action, prominent design)
   - Forgot password link
   - Sign up link for new users

## Behavior

- Email field validates for proper email format
- Password field is masked by default with option to show
- Form validates both fields are filled before submission
- Error messages appear below the relevant field
- Successful login redirects to the appropriate dashboard based on user role
- "Enter" key submits the form

## Visual Style

- Clean, minimalist design with ample white space
- Soft shadows for form container
- Royal blue primary button with white text
- Light gray background with white form container
- System logo at the top (optional)

## Responsive Considerations

- Form container adjusts width based on screen size
- Full-width inputs and buttons on mobile devices
- Maintains readability on all screen sizes
