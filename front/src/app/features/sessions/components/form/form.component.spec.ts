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
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
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

import { NgZone } from "@angular/core";


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
  
  const mockAdminSessionService = {
    sessionInformation: {
      admin: true
    }
  } 
  const mockSessionService = {
    sessionInformation: {
      admin: false
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
        NoopAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockAdminSessionService }
      ],
      declarations: [FormComponent]
    })
    TestBed.overrideProvider(SessionApiService, {useValue: {
      create: jest.fn(() => of({})),
    }});
    TestBed.overrideProvider(TeacherService, {useValue: {all: jest.fn(() => of([mockTeacher]))}})

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
      const ngZone = TestBed.inject(NgZone);

      component.sessionForm?.setValue({ // TODO : FIX
        name: 'test',
        date: new Date('2023-01-01'),
        teacher_id: 1,
        description: 'test',
      });

      ngZone.run(() => {
        component.submit();
    });
      expect(sessionCreate).toHaveBeenCalled();
      expect(snackBarSpy).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
    });

    it('should disable the submit button if any field is empty and enable it if all are filled', () => {
      const submitButton = fixture.nativeElement.querySelector('button[type="submit"]');
      
      //Initially, the form should be invalid and the button should be disabled.
      expect(component.sessionForm?.valid).toBeFalsy();
      expect(submitButton.disabled).toBeTruthy();

      // Fill the form with data
      component.sessionForm?.controls['name'].setValue('test');
      component.sessionForm?.controls['date'].setValue(new Date('2023-01-01'));
      component.sessionForm?.controls['teacher_id'].setValue(1);
      component.sessionForm?.controls['description'].setValue('test');
      fixture.detectChanges();

      // Now the form should be valid, and the button should be enabled
      expect(component.sessionForm?.valid).toBeTruthy();
      expect(submitButton.disabled).toBeFalsy();

      // Make the form invalid by emptying the name field
      component.sessionForm?.controls['name'].setValue('');
      fixture.detectChanges();

      // Now the form should be invalid and the button disabled again
      expect(component.sessionForm?.valid).toBeFalsy();
      expect(submitButton.disabled).toBeTruthy();
    });
  });
});
