import { HttpClientModule } from "@angular/common/http";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { FormBuilder, ReactiveFormsModule, FormGroup } from "@angular/forms";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { expect } from "@jest/globals";

import { RegisterComponent } from "./register.component";
import { AuthService } from "../../services/auth.service";
import { of, throwError } from "rxjs";

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegisterComponent ],
      imports: [
        BrowserAnimationsModule, 
        HttpClientModule, 
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
    })

      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it("should display error messages when mandatory fields are missing", () => {
    const form = component.form as FormGroup;
    form.setValue({
      email: "",
      password: "",
      username: "",
    });
    component.submit();

    expect(form.controls["email"].hasError("required")).toBeTruthy();
    expect(form.controls["password"].hasError("required")).toBeTruthy();
    expect(form.controls["username"].hasError("required")).toBeTruthy();
  });

  it("should register successfully", () => {
    const authService = TestBed.inject(AuthService);
    const registerSpy = jest
      .spyOn(authService, "register")
      .mockReturnValue(of({ message: "User registered successfully!" } as any)); //TODO : fix that

    const form = component.form as FormGroup;
    form.setValue({
      email: "test@test.com",
      password: "password",
      username: "testuser",
    });
    component.submit();

    expect(registerSpy).toHaveBeenCalledWith({
      email: "test@test.com",
      password: "password",
      userName: "testuser",
    });

    expect(component.onError).toEqual(false);
  });
  it("should handle error during registration", () => {
    const authService = TestBed.inject(AuthService);
    const registerSpy = jest
      .spyOn(authService, "register")
      .mockReturnValue(throwError(() => new Error("Registration failed")));

    const form = component.form as FormGroup;
    form.setValue({
      email: "test@test.com",
      password: "password",
      username: "testuser",
    });

    component.submit();
    expect(component.onError).toEqual(true);
  });
});
