import { HttpClientTestingModule } from '@angular/common/http/testing';
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
      admin: true,
    }
  };

  const mockUserSessionService = {
    sessionInformation: {
      admin: false,
    },
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
    findAll: jest.fn().mockReturnValue(of(mockSessions)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [HttpClientTestingModule, MatCardModule, MatIconModule],
      providers: [
        { provide: SessionService, useValue: mockAdminSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    sessionApiService = TestBed.inject(SessionApiService);
    sessionService = TestBed.inject(SessionService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of sessions', () => {
    expect(sessionApiService.all).toHaveBeenCalled();
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelectorAll('mat-card').length).toBe(2);
  });

  it('should show Create and Detail buttons if the logged-in user is an admin', () => {
    sessionService = TestBed.inject(SessionService);
    expect(sessionService.sessionInformation?.admin).toBeTruthy();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('#create')).toBeTruthy();
    expect(compiled.querySelector('#detail')).toBeTruthy();
  });

  it('should not show Create button if the logged-in user is not an admin', () => {
    TestBed.overrideProvider(SessionService, {
      useValue: mockUserSessionService,
    });
    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    sessionService = TestBed.inject(SessionService);
    expect(sessionService.sessionInformation?.admin).toBeFalsy();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('#create')).toBeFalsy();
    expect(compiled.querySelector('#detail')).toBeTruthy();
  });
});
