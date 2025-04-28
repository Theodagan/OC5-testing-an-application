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
import { Teacher } from "../interfaces/teacher.interface";

import { TeacherService } from './teacher.service';

import  { expect } from '@jest/globals';


describe('TeacherService', () => {
  let service: TeacherService;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;
  const mockTeacher: Teacher = {
    id: 1,
    firstName: "test",
    lastName: "test",
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, HttpClientTestingModule],
    });
    service = TestBed.inject(TeacherService);
    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should return all teachers", () => {
    const mockTeachers: Teacher[] = [mockTeacher];
    service.all().subscribe((teachers) => {
      expect(teachers.length).toBe(1);
      expect(teachers[0]).toEqual(mockTeacher);
    });
    const req = httpTestingController.expectOne("api/teacher");
    expect(req.request.method).toEqual("GET");
    req.flush(mockTeachers);
  });

  it("should return teacher details", () => {
    service.detail("1").subscribe((teacher) => {
      expect(teacher).toEqual(mockTeacher);
    });
    const req = httpTestingController.expectOne("api/teacher/1");
    expect(req.request.method).toEqual("GET");
    req.flush(mockTeacher);
  });
});
