import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router, Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing'

import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { SessionService } from 'src/app/services/session.service';

import { LoginComponent } from './login.component';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let router: Router;
  let sessionService: SessionService;

  const routes: Routes = [
    { path: 'me', component: LoginComponent }
  ];

  beforeEach(async () => {
    const authServiceMock = {
      login: jest.fn()
    };
    const sessionServiceMock = {
      setSession: jest.fn()
    }


    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
        FormBuilder

      ],
      imports: [
        RouterTestingModule.withRoutes(routes),

        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule]
    })
      .compileComponents();
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    authService = TestBed.inject(AuthService);
    sessionService = TestBed.inject(SessionService)
    router = TestBed.inject(Router);

  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should login successfully', async () => {
    // Arrange
    const mockResponse: SessionInformation = {
      token: 'mockToken',
      id: 1,
      username: 'test',
      admin: true,
      type: '',
      firstName: '',
      lastName: ''
    };
    jest.spyOn(authService, 'login').mockReturnValue(of(mockResponse));
    jest.spyOn(router, 'navigate').mockResolvedValue(true);

    // Act
    component.form.controls['email'].setValue('test@example.com');
    component.form.controls['password'].setValue('password');
    component.submit();
    fixture.detectChanges();
    await fixture.whenStable();

    // Assert
    expect(authService.login).toHaveBeenCalledWith({ email: 'test@example.com', password: 'password' });
    expect(router.navigate).toHaveBeenCalledWith(['/me']);
    expect(authService.login).toHaveReturnedWith(of(mockResponse))
    //expect(sessionService.setSession).toHaveBeenCalled();
  });

  it('should handle incorrect login credentials', async () => {
    // Arrange
    jest.spyOn(authService, 'login').mockReturnValue(throwError(() => ({ status: 401 })));

    // Act
    component.form.controls['email'].setValue('wrong@example.com');
    component.form.controls['password'].setValue('wrongpassword');
    component.submit();
    fixture.detectChanges();
    await fixture.whenStable();

    // Assert
    expect(component.onError).toEqual(true);
  });


  it('should display error for missing required fields', () => {
    // Arrange
    component.form.controls['email'].setValue('');
    component.form.controls['password'].setValue('');
    component.form.controls['email'].markAsTouched();
    component.form.controls['password'].markAsTouched();

    // Act
    component.submit();

    // Assert
    expect(component.form.controls['email'].hasError('required')).toBeTruthy();
    expect(component.form.controls['password'].hasError('required')).toBeTruthy();
    expect(component.form.invalid).toEqual(true);

  });

});
