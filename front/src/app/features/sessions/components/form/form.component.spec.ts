import { HttpErrorResponse } from '@angular/common/http';
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
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { ActivatedRoute, Router, Routes } from '@angular/router';



describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let sessionApiService: SessionApiService;
  let teacherService: TeacherService;
  let snackBar: MatSnackBar;
  let router: Router;

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
  const mockUserSessionService = {
    sessionInformation: {
      admin: false
    }
  } 

  beforeEach(async () => {
    await setup(mockAdminSessionService);
    await fixture.detectChanges();
    jest.clearAllMocks();
  });

  async function setup(serviceMock: any) {
    TestBed.resetTestingModule();
    await TestBed.configureTestingModule({

      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
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
        { provide: SessionService, useValue: serviceMock },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: jest.fn(() => null)
              }
            }
          }
        }
      ],
      declarations: [FormComponent]
    }).compileComponents();
    
    TestBed.overrideProvider(SessionApiService, {useValue: {create: jest.fn(() => of({})),update: jest.fn(() => of({})),}});
    TestBed.overrideProvider(TeacherService, {useValue: {all: jest.fn(() => of([mockTeacher]))}}).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    
    fixture.detectChanges();
  }

  async function setupWithRoutes(mockSession: Session) {
    TestBed.resetTestingModule();
    await TestBed.configureTestingModule({
      declarations: [FormComponent],
      imports: [
        RouterTestingModule.withRoutes([
          { path: 'sessions/update/:id', component: FormComponent }
        ]),
        HttpClientTestingModule,
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
        {
          provide: SessionService,
          useValue: { sessionInformation: { admin: true } }
        },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: jest.fn((key: string) => key === 'id' ? '123' : null)
              }
            }
          }
        },
        {
          provide: SessionApiService,
          useValue: {
            detail: jest.fn(() => of(mockSession)),
            create: jest.fn(),
            update: jest.fn()
          }
        },
        {
          provide: TeacherService,
          useValue: {
            all: jest.fn(() => of([mockTeacher]))
          }
        }
      ]
    }).compileComponents();
  
    const fixture = TestBed.createComponent(FormComponent);
    const component = fixture.componentInstance;
    const router = TestBed.inject(Router);
    const ngZone = TestBed.inject(NgZone);
    const sessionApiService = TestBed.inject(SessionApiService);
  
    return { fixture, component, router, ngZone, sessionApiService };
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  describe('ngOnInit', () => {
    it('should navigate to /sessions when user is not admin', async () => {
      await setup(mockUserSessionService);
      const ngZone = TestBed.inject(NgZone);
      const navigateSpy = jest.spyOn(router, 'navigate');

      ngZone.run(() => { component.ngOnInit() });
      expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
    });
    
    

    it("should set onUpdate to true, extract id, call detail, and initForm when id is in route", async () => {
      const mockSession: Session = {
        id: 1,
        name: 'Mock Session',
        description: 'Mock Description',
        date: new Date('2023-01-01'),
        teacher_id: 1,
        users: [1, 2],
        createdAt: new Date(),
        updatedAt: new Date()
      };
    
      const { fixture, component, router, ngZone, sessionApiService } = await setupWithRoutes(mockSession);
      const initFormSpy = jest.spyOn(component as any, 'initForm');
    
      await ngZone.run(() => router.navigate(['/sessions/update/123']));
      fixture.detectChanges();
    
      expect(component.onUpdate).toBe(true);
      expect((component as any).id).toBe('123');
      expect(sessionApiService.detail).toHaveBeenCalledWith('123');
      expect(initFormSpy).toHaveBeenCalledWith(mockSession);
    });




    it("should set onUpdate to false and when there is not update in the url",  () => {
      const ngZone = TestBed.inject(NgZone);
      ngZone.run(() => {
        TestBed.inject(Router).navigate(['/create']).then(() => {
            const url = TestBed.inject(Router).url;
            expect(url).not.toContain('update');
            expect(component.onUpdate).toBeFalsy();
            fixture.detectChanges();
            expect(component.sessionForm).toBeDefined()
        });
      });
    });
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

    it('should update a session', () => {
      const sessionUpdate = jest.spyOn(sessionApiService, 'update').mockReturnValue(of({} as Session));
      const snackBarSpy = jest.spyOn(snackBar, 'open');
      const ngZone = TestBed.inject(NgZone);

      component.onUpdate = true;

      component.sessionForm?.setValue({ // TODO : FIX
        name: 'test',
        date: new Date('2023-01-01'),
        teacher_id: 1,
        description: 'test',
      });

      ngZone.run(() => {
        component.submit();
      });

      expect(sessionUpdate).toHaveBeenCalled();
      expect(snackBarSpy).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
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
