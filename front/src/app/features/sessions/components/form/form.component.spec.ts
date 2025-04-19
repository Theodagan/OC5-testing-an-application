import { HttpErrorResponse } from '@angular/common/http';
import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {  ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';
import { Teacher } from 'src/app/interfaces/teacher.interface';
import { SessionService } from 'src/app/services/session.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';

import { FormComponent } from './form.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Session } from '../../interfaces/session.interface';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let sessionApiService: SessionApiService;
  let teacherService: TeacherService;
  let snackBar: MatSnackBar;
  const mockTeacher: Teacher = {
    id: 1, 
    firstName: 'teacher', 
    lastName: 'teacher',
    createdAt: new Date("2023-01-01"),
    updatedAt: new Date("2023-02-01")
  }
  
  const mockSessionService = {
    sessionInformation: {
      admin: true
    }
  } 

  beforeEach(async () => {
    await TestBed.configureTestingModule({

      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule, 
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService }
      ],
      declarations: [FormComponent]
    })
    TestBed.overrideProvider(SessionApiService, {useValue: {
      create: jest.fn(() => of({})),
    }});
    TestBed.overrideProvider(TeacherService, {useValue: {findAll: jest.fn(() => of([mockTeacher]))}})

      .compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    component.ngOnInit();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('onSubmit', () => {
    beforeEach(() => {
      sessionApiService = TestBed.inject(SessionApiService);
      teacherService = TestBed.inject(TeacherService);
      snackBar = TestBed.inject(MatSnackBar);

      jest.spyOn(teacherService, 'all').mockReturnValue(of([mockTeacher]));
    })

    it('should create a session', () => {
      const sessionCreate = jest.spyOn(sessionApiService, 'create').mockReturnValue(of({} as Session));
      const snackBarSpy = jest.spyOn(snackBar, 'open');
      component.sessionForm?.setValue({ // TODO : FIX
        name: 'test',
        date: '2023-01-01',
        teacher: '1',
        description: 'test',
      });
      component.submit();
      expect(sessionCreate).toHaveBeenCalled();
      expect(snackBarSpy).toHaveBeenCalledWith('Session created', 'Close', { duration: 3000 });
    });

    it('should display an error if a mandatory field is missing', () => {
      const sessionCreate = jest.spyOn(sessionApiService, 'create').mockReturnValue(throwError(() => new HttpErrorResponse({
        status: 400,
        statusText: 'Bad Request'
      })));
      const snackBarSpy = jest.spyOn(snackBar, 'open');

      component.submit();
      expect(sessionCreate).toHaveBeenCalled();
      expect(snackBarSpy).toHaveBeenCalledWith('Error creating session', 'Close', { duration: 3000 });
    });
  });


});
