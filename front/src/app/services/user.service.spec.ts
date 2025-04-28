import {
  HttpClient,
  HttpClientModule,
  HttpErrorResponse,
} from "@angular/common/http";
import {
  HttpClientTestingModule,
  HttpTestingController,
} from "@angular/common/http/testing";
import { TestBed } from '@angular/core/testing';
import { User } from "../interfaces/user.interface";

import { UserService } from './user.service';

import  { expect } from '@jest/globals';


describe('UserService', () => {
  let service: UserService;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;
  const mockUser: User = {
    id: 1,
    email: "test",
    firstName: "test",
    lastName: "test",
    admin: false,
    createdAt: new Date('2023-10-27T10:00:00.000Z'),
    updatedAt: new Date('2023-10-27T10:00:00.000Z'),
    password: "mockPassword"
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
    });
    service = TestBed.inject(UserService);
    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it("should return user details", () => {
    service.getById("1").subscribe((user) => {
      expect(user).toEqual(mockUser);
    });

    const req = httpTestingController.expectOne("api/user/1");
    expect(req.request.method).toEqual("GET");
    req.flush(mockUser);
  });

  it("should delete a user", () => {
    service.delete("1").subscribe();
    const req = httpTestingController.expectOne("api/user/1");
    expect(req.request.method).toEqual("DELETE");
    req.flush({});
  });
});
