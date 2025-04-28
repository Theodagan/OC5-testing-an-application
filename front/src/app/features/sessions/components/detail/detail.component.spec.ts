import { HttpClientModule } from "@angular/common/http";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ReactiveFormsModule } from "@angular/forms";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { RouterTestingModule, } from "@angular/router/testing";
import { expect, jest } from "@jest/globals";
import { SessionService } from "../../../../services/session.service";

import { DetailComponent } from "./detail.component";
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
    sessionInformation : {
      isAdmin: true,
      sessionInformation: { admin: true, id: 1 },
    }
  };

  const mockUserSessionService = {
    sessionInformation : {
      isAdmin: true,
      sessionInformation: { admin: true, id: 1 },
    }
  };

  async function setup(serviceMock: any) {
    await TestBed.configureTestingModule({
        imports: [
            RouterTestingModule,
            HttpClientModule,
            MatSnackBarModule,

            ReactiveFormsModule
        ],
        declarations: [DetailComponent],
        providers: [{ provide: SessionService, useValue: serviceMock }],
    }).compileComponents();

    sessionService = TestBed.inject(SessionService);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule
      ],
      declarations: [DetailComponent], 
      providers: [{ provide: SessionService, useValue: mockUserSessionService }],
    })
      .compileComponents();
      sessionService = TestBed.inject(SessionService);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  describe("display session information", () => {
    it("should display session information correctly", async () => {
        await setup(mockAdminSessionService);
        fixture.detectChanges();
        await fixture.whenStable();
        component.session = mockSession
        expect(component.session.id).toEqual("1");
        expect(component.session.name).toEqual("Session 1");
        expect(component.session.date).toEqual(new Date("2024-01-15"));
        expect(component.session.description).toEqual("Description 1");
        expect(component.session.teacher_id).toEqual("1");
      });
      it("should display delete button when user is admin", async () => {   
        await setup(mockAdminSessionService);
        fixture.detectChanges();
        component.session = mockSession
        const deleteButton = fixture.nativeElement.querySelector('.delete-button');
        expect(deleteButton).toBeTruthy();
    });
  });

  describe("session deletion", () => {
    it("should delete session correctly", async () => {
        await setup(mockUserSessionService);
        fixture.detectChanges();
        component.session = mockSession
        component.delete();
        await fixture.whenStable();
        //expect(mockUserSessionService.openSnackBar).toHaveBeenCalledWith("Session deleted !", "OK");

    });
  });

});


