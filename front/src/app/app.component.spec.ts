import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { of, throwError, first } from 'rxjs';

import { AppComponent } from './app.component';
import { UserService } from './services/user.service';
import { AuthService } from './features/auth/services/auth.service';
import { expect } from '@jest/globals';

import { SessionService } from 'src/app/services/session.service';


import { NgZone } from "@angular/core";




const mockSessionService = {
  logOut: jest.fn().mockReturnValue(undefined),
  $isLogged: jest.fn().mockReturnValue(of(true))
};

const mockAuthService = {
  logout: jest.fn().mockReturnValue(of(undefined)),
};


describe('AppComponent', () => {

  const mockUserService = {
    isLogged: jest.fn().mockReturnValue(true)
  };
  let app: AppComponent;
  let fixture;
  beforeEach(async () => {
    jest.clearAllMocks();
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        MatToolbarModule
      ],
      declarations: [
        AppComponent
      ],
      providers: [
        {
          provide: UserService,
          useValue: mockUserService
        },
        { provide: AuthService, useValue: mockAuthService },
        { provide: SessionService, useValue: mockSessionService }
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    app = fixture.componentInstance;
  });

  it('should create the app', () => {
    expect(app).toBeTruthy();
  });

  it('should call logOut() from the sessionService and navigate when logout() is called', () => {
    const ngZone = TestBed.inject(NgZone);
    ngZone.run(() => {app.logout();});

    expect(mockSessionService.logOut).toHaveBeenCalledTimes(1);
    
  });

  it('should call $isLogged() from the sessionService when $isLogged() is called', (done) => {

    app.$isLogged().pipe(first()).subscribe(value => {
      expect(value).toBe(true);
      expect(mockSessionService.$isLogged).toHaveBeenCalledTimes(1);
      done();
    });
  });


});
