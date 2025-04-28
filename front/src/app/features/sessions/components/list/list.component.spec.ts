import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { Session } from '../../interfaces/session.interface';
import { SessionApiService } from '../../services/session-api.service';

import { ListComponent } from './list.component';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  let sessionApiService: SessionApiService;
  let sessionService: SessionService;

  const mockAdminSessionService = {
    sessionInformation: {
      id: 1,
      admin: true
    }
  };

  const mockUserSessionService = {
    sessionInformation: {
      id: 2, 
      admin: false,
    }
  };

  const mockSessions: Session[] = [
    {
      id: 1,
      name: 'Session 1',
      date: new Date('2024-01-01'),
      description: 'Description 1',
      teacher_id: 1,
      users: [],
    },
    {
      id: 2,
      name: 'Session 2',
      date: new Date('2024-02-01'),
      description: 'Description 2',
      teacher_id: 2,
      users: [],
    },   
  ];

  const mockSessionApiService = {
    all: jest.fn().mockReturnValue(of(mockSessions)),
  };

  async function setup(serviceMock: any) {
    TestBed.resetTestingModule();
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [HttpClientTestingModule, MatCardModule, MatIconModule, RouterTestingModule],
      providers: [
        { provide: SessionService, useValue: serviceMock },
        { provide: SessionApiService, useValue: mockSessionApiService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    sessionApiService = TestBed.inject(SessionApiService);
    sessionService = TestBed.inject(SessionService);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await setup(mockUserSessionService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of sessions', () => {
    expect(sessionApiService.all).toHaveBeenCalled();
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelectorAll('mat-card').length).toBe(mockSessions.length + 1); // +1 for the mat-card wrapper
  });

  it('should show Create and Detail buttons if the logged-in user is an admin', async () => {
    await setup(mockAdminSessionService);
    fixture.detectChanges();

    sessionService = TestBed.inject(SessionService);
    expect(sessionService.sessionInformation?.admin).toBeTruthy();
    expect(isButtonVisble(fixture, "Detail")).toBeTruthy();
    expect(isButtonVisble(fixture, "Edit")).toBeTruthy();
  });

  it('should not show Create button if the logged-in user is not an admin', async () => {
    await setup(mockUserSessionService);
    fixture.detectChanges();

    sessionService = TestBed.inject(SessionService);
    expect(sessionService.sessionInformation?.admin).toBeFalsy();
    expect(isButtonVisble(fixture, "Detail")).toBeTruthy();
    expect(isButtonVisble(fixture, "Edit")).toBeFalsy();
  });
});

function isButtonVisble (fixture: ComponentFixture<any>, buttonText: string){
  const domButtons = fixture.nativeElement.querySelectorAll('button[mat-raised-button] span.ml1');
  let isButtonVisible = false;
  for (let i = 0; i < domButtons.length; i++){
    if(domButtons[i].textContent === buttonText){
      isButtonVisible = true;
    }
  }
  return isButtonVisible;
}