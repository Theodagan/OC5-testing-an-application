import { HttpClientModule } from "@angular/common/http";
import { ComponentFixture, TestBed, fakeAsync, tick } from "@angular/core/testing";
import { MatCardModule } from "@angular/material/card";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input"; 
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";
import { Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { SessionService } from "src/app/services/session.service";
import { UserService } from "src/app/services/user.service";

import { MeComponent } from "./me.component";
import { expect } from "@jest/globals";
import { of } from "rxjs";


describe("MeComponent", () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let userService: UserService;
  let sessionService: SessionService;
  let snackBar: MatSnackBar;
  let router: Router;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    },
    logOut: jest.fn()
  };

  const mockUserService = {
    delete: jest.fn(() => of({})),
    getById: jest.fn(() => of({}))
  };
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],      
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService}
      ],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        RouterTestingModule.withRoutes([])
      ],
    })
      .compileComponents();
    userService = TestBed.inject(UserService);
    sessionService = TestBed.inject(SessionService);
    snackBar = TestBed.inject(MatSnackBar);
    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should call history.back', () => {
    const historyBackSpy = jest.spyOn(window.history, 'back');
    component.back();
    expect(historyBackSpy).toHaveBeenCalled();
  });

  it('should delete the account, display a snackbar, logout and navigate to /',  async() => {
    const userServiceDeleteSpy = jest.spyOn(userService, 'delete');
    const snackBarSpy = jest.spyOn(snackBar, 'open');
    const sessionServiceLogoutSpy = jest.spyOn(sessionService, 'logOut');
    const routerSpy = jest.spyOn(router, 'navigate');

    await component.delete();
    await fixture.whenStable();

    expect(userServiceDeleteSpy).toHaveBeenCalled();

    userServiceDeleteSpy.mock.results[0].value.subscribe(() => {
      expect(snackBarSpy).toHaveBeenCalledWith("Your account has been deleted !", 'Close', { duration: 3000 });
      expect(sessionServiceLogoutSpy).not.toHaveBeenCalled();
      expect(routerSpy).toHaveBeenCalledWith(['/']);
    })
  });


});

