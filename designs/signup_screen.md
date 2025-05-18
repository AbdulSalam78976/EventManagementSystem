# Signup Screen

## Purpose
Allow new users to create an account with the appropriate role.

## Design

```
+------------------------------------------------------+
|                                                      |
|                                                      |
|        +----------------------------------+          |
|        |                                  |          |
|        |     EVENT MANAGEMENT SYSTEM      |          |
|        |                                  |          |
|        +----------------------------------+          |
|                                                      |
|        +----------------------------------+          |
|        |         Create Account           |          |
|        +----------------------------------+          |
|                                                      |
|        +----------------------------------+          |
|        | Full Name:                       |          |
|        | [                              ] |          |
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
|        | Confirm Password:                |          |
|        | [                              ] |          |
|        +----------------------------------+          |
|                                                      |
|        +----------------------------------+          |
|        | Role:                            |          |
|        | [Attendee ▼                    ] |          |
|        +----------------------------------+          |
|                                                      |
|        +----------------------------------+          |
|        |           [ SIGN UP ]            |          |
|        +----------------------------------+          |
|                                                      |
|        +----------------------------------+          |
|        | Already have an account? Login   |          |
|        +----------------------------------+          |
|                                                      |
+------------------------------------------------------+
```

## Elements

1. **Header**
   - System name in large, bold font
   - "Create Account" subtitle

2. **Signup Form**
   - Full Name input field
   - Email input field with validation
   - Password input field with show/hide toggle and strength indicator
   - Confirm Password field
   - Role selection dropdown (Attendee, External Event Organizer)
   - Error message area

3. **Action Buttons**
   - Sign Up button (primary action, prominent design)
   - Login link for existing users

## Behavior

- All fields are required
- Email field validates for proper email format
- Password fields must match and meet minimum security requirements
- Form validates all fields before submission
- Error messages appear below the relevant field
- Successful signup redirects to login page with success message
- Role dropdown defaults to "Attendee"

## Visual Style

- Consistent with login screen
- Clean, minimalist design with ample white space
- Soft shadows for form container
- Royal blue primary button with white text
- Light gray background with white form container
- System logo at the top (optional)

## Responsive Considerations

- Form container adjusts width based on screen size
- Full-width inputs and buttons on mobile devices
- Maintains readability on all screen sizes
