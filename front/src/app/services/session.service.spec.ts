import { TestBed } from "@angular/core/testing";
import { SessionInformation } from "../interfaces/sessionInformation.interface";

import { SessionService } from "./session.service";

import  { expect } from '@jest/globals';


describe("SessionService", () => {
  let service: SessionService;

  const mockSessionInformation: SessionInformation = {
    id: 1,
    username: "test",
    firstName: "test",
    lastName: "test",
    admin: false,
    token: "",
    type: ""
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should return the login state", () => {
    service.$isLogged().subscribe((isLogged) => {
      expect(isLogged).toBe(false);
    });
  });

  it("should log in a user", () => {
    service.logIn(mockSessionInformation);
    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(mockSessionInformation);
    service.$isLogged().subscribe((isLogged) => {
      expect(isLogged).toBe(true);
    });
  });

  it("should log out a user", () => {
    service.logIn(mockSessionInformation);
    service.logOut();
    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
    service.$isLogged().subscribe((isLogged) => {
      expect(isLogged).toBe(false);
    });
  });
});
