import { HttpClient, HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { expect } from '@jest/globals';


import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpTestingController: HttpTestingController;
  let httpClient: HttpClient;

  const mockSessionInformation: SessionInformation = {
      id: 1,
      firstName: 'test',
      lastName: 'test',
      admin: false,
      token: '',
      type: '',
      username: ''
  };

  const mockLoginRequest: LoginRequest = {
    email: 'test',
    password: 'password',
  };

  const mockRegisterRequest: RegisterRequest = {
    email: 'test',
    password: 'password',
    firstName: 'test',
    lastName: 'test',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
    });
    service = TestBed.inject(AuthService);
    httpTestingController = TestBed.inject(HttpTestingController);
    httpClient = TestBed.inject(HttpClient);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should register a user', () => {
    service.register(mockRegisterRequest).subscribe();

    const req = httpTestingController.expectOne('api/auth/register');
    expect(req.request.method).toEqual('POST');
    expect(req.request.body).toEqual(mockRegisterRequest);
    req.flush({});
  });

  it('should login a user', () => {
    service.login(mockLoginRequest).subscribe((sessionInformation) => {
      expect(sessionInformation).toEqual(mockSessionInformation);
    });

    const req = httpTestingController.expectOne('api/auth/login');
    expect(req.request.method).toEqual('POST');
    expect(req.request.body).toEqual(mockLoginRequest);
    req.flush(mockSessionInformation);
  });
});