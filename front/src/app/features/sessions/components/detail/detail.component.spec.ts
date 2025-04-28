import { HttpClientTestingModule } from "@angular/common/http/testing";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ReactiveFormsModule } from "@angular/forms";
import { MatSnackBarModule} from "@angular/material/snack-bar";
import { RouterTestingModule } from "@angular/router/testing";
import { expect } from "@jest/globals";
import { SessionService } from "../../../../services/session.service";
import { MatCardModule } from "@angular/material/card";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";

import { DetailComponent} from "./detail.component";
import { Session } from "../../interfaces/session.interface";


describe("DetailComponent", () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let sessionService: SessionService;

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
      admin: true,
    },
    matSnackBar: {
      open: jest.fn(),
    },
  };

  const mockUserSessionService = {
    sessionInformation: {
      isAdmin: false,
      id: 2, 
      admin: false,
    }, matSnackBar: {
      open: jest.fn(),
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
        providers: [{ provide: SessionService, useValue: serviceMock }],
    }).compileComponents();

    sessionService = TestBed.inject(SessionService);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
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
          component.isAdmin = true;
          fixture.detectChanges();
          component.session = mockSession
          const deleteButtons = fixture.nativeElement.querySelectorAll('button[mat-raised-button][color="warn"] span.ml1');
          let isDeleteButtonVisible;
          for (let i = 0; i < deleteButtons.length; i++){
            if(deleteButtons[i].textContent === 'Delete'){
              isDeleteButtonVisible = true;
            }
          }
          expect(isDeleteButtonVisible).toBeTruthy();
    });
  });
  
  describe("session deletion", () => {
    it("should delete session correctly", async () => {
      await setup(mockAdminSessionService);
      component.session = mockSession;
      fixture.detectChanges();
        
      component.delete();
      await fixture.whenStable();
      expect(mockAdminSessionService.matSnackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    });
  });

});


