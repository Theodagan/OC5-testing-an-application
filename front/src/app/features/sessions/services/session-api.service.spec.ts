import {
  HttpClient,
  HttpErrorResponse,
  HttpClientModule,
} from "@angular/common/http";
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { Session } from "../interfaces/session.interface";

import { SessionApiService } from "./session-api.service";
import { expect } from "@jest/globals";


describe("SessionApiService", () => {
  let service: SessionApiService;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;
  const mockSession: Session = {
    id: 1,
    name: "test",
    date: new Date("12/12/2024"),
    description: "test",
    teacher_id: 1,
    users: [],
    createdAt: new Date("22/12/2024"),
    updatedAt: new Date("23/12/2024")
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
    });
    service = TestBed.inject(SessionApiService);
    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
      httpTestingController.verify();
  });

  it("should be created", () => {
      expect(service).toBeTruthy();
  });

  it("should return all sessions", () => {
    const mockSessions: Session[] = [mockSession];

    service.all().subscribe((sessions) => {
      expect(sessions.length).toBe(1);
      expect(sessions[0]).toEqual(mockSession);
      });

      const req = httpTestingController.expectOne("api/session");
      expect(req.request.method).toEqual("GET");
    req.flush(mockSessions);
  });

  it("should return session details", () => {
    service.detail("1").subscribe((session) => {
      expect(session).toEqual(mockSession);
    });
  
      const req = httpTestingController.expectOne("api/session/1");
      expect(req.request.method).toEqual("GET");
      req.flush(mockSession);
  });

  it("should delete a session", () => {
    service.delete("1").subscribe();

    const req = httpTestingController.expectOne("api/session/1");
    expect(req.request.method).toEqual("DELETE");
      req.flush({});
  });

   it("should create a session", () => {
    service.create(mockSession).subscribe((session) => {
      expect(session).toEqual(mockSession);
    });

    const req = httpTestingController.expectOne("api/session");
    expect(req.request.method).toEqual("POST");
    req.flush(mockSession);
  });

  it("should update a session", () => {
    service.update("1", mockSession).subscribe((session) => {
      expect(session).toEqual(mockSession);
    });

    const req = httpTestingController.expectOne("api/session/1");
    expect(req.request.method).toEqual("PUT");
    req.flush(mockSession);
  });

  it("should participate to session", () => {
    service.participate("1", "1").subscribe();

      const req = httpTestingController.expectOne("api/session/1/participate/1");
      expect(req.request.method).toEqual("POST");
    req.flush({});
  });

  it("should unparticipate to session", () => {
    service.unParticipate("1", "1").subscribe();

    const req = httpTestingController.expectOne(
      "api/session/1/participate/1"
    );
    expect(req.request.method).toEqual("DELETE");
    req.flush({});
  });

  it('should handle errors', () => {
    const errorResponse = new HttpErrorResponse({ status: 404, statusText: 'Not Found' });
    service.all().subscribe({ error: (error) => expect(error).toEqual(errorResponse) });

    const req = httpTestingController.expectOne('api/session');
    req.flush('error', errorResponse);
   });
});

