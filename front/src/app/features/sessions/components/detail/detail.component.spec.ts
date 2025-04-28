import { HttpClientTestingModule } from "@angular/common/http/testing";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ReactiveFormsModule } from "@angular/forms";
import { MatSnackBar, MatSnackBarModule} from "@angular/material/snack-bar";
import { RouterTestingModule } from "@angular/router/testing";
import { expect } from "@jest/globals";
import { SessionService } from "../../../../services/session.service";
import { MatCardModule } from "@angular/material/card";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";

import { DetailComponent} from "./detail.component";
import { Session } from "../../interfaces/session.interface";
import { SessionApiService } from "../../services/session-api.service";
import { of } from "rxjs";


describe("DetailComponent", () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let sessionService: SessionService;
  let sessionApiService: SessionApiService; // Added this line
  let snackBar: MatSnackBar;


  const mockSession: Session = {
    id: 1,
    name: "Session 1",
    date: new Date("2024-01-15"),
    description: "Description 1",
    users: [],
    teacher_id: 1,
  };

  const mockAdminSessionService = {
    sessionInformation: {
      isAdmin: true,
      id: 1,
      admin: true
    }
  };

  const mockSessionApiService = {
    detail: jest.fn(() => of(mockSession)),
    delete: jest.fn(() => of({})),
    participate: jest.fn(()=> of({})),
    unParticipate: jest.fn(() => of({})),
  };

  const mockUserSessionService = {
    sessionInformation: {
      isAdmin: false,
      id: 2, 
      admin: false,
    }
  };


  async function setup(serviceMock: any) {
    TestBed.resetTestingModule();
    await TestBed.configureTestingModule({
        imports: [
            RouterTestingModule,
            HttpClientTestingModule,
            MatSnackBarModule,
            ReactiveFormsModule,
            MatCardModule,
            MatIconModule,
            MatButtonModule,
        ],
        declarations: [DetailComponent], 
        providers: [
          { provide: SessionService, useValue: serviceMock },
          { provide: SessionApiService, useValue: mockSessionApiService}
        ]
    }).compileComponents();

    sessionService = TestBed.inject(SessionService);
    sessionApiService = TestBed.inject(SessionApiService);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    snackBar = TestBed.inject(MatSnackBar);
    component.sessionId = '1';
    fixture.detectChanges();
    component.ngOnInit();
  }

  beforeEach(async () => {
    await setup(mockUserSessionService);
    await fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  describe("display session information", () => {
    it("should display session information correctly", async () => {
        component.session = mockSession;
        await fixture.detectChanges();
        expect(component.session.id).toEqual(1);
        expect(component.session.name).toEqual("Session 1");
        expect(component.session.date).toEqual(new Date("2024-01-15"));
        expect(component.session.description).toEqual("Description 1");
        expect(component.session.teacher_id).toEqual(1);
      });

      it("should display delete button when user is admin", async () => {   
          await setup(mockAdminSessionService);
          component.session = mockSession;
          fixture.detectChanges();

        const domButtons = fixture.nativeElement.querySelectorAll('button[mat-raised-button][color="warn"] span.ml1');
          let isDeleteButtonVisible = false;
          for (let i = 0; i < domButtons.length; i++){
            if(domButtons[i].textContent === 'Delete'){
              isDeleteButtonVisible = true;
            }
          }
          expect(isDeleteButtonVisible).toBeTruthy();
    });
  });
  
  describe("session deletion", () => {
    it("should delete session correctly", async () => {
      await setup(mockAdminSessionService);
      const sessionDeleteSpy = jest.spyOn(sessionApiService, 'delete');
      const snackBarSpy = jest.spyOn(snackBar, 'open');
    
      component.delete();
    
      await fixture.whenStable();
    
      expect(sessionDeleteSpy).toHaveBeenCalledWith('1');
      expect(snackBarSpy).toHaveBeenCalledWith(
        "Session deleted !",
        "Close",
        { duration: 3000 }
      );
    });
  });

});


