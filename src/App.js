import Register from "./pages/Register";
import Dashboard from "./pages/admin/Dashboard";
import Karyawan from "./pages/admin/masterdata/Karyawan";
import Jabatan from "./pages/admin/masterdata/Jabatan";
import Shift from "./pages/admin/masterdata/Shift";
import Lokasi from "./pages/admin/masterdata/Lokasi";
import Organisasi from "./pages/admin/masterdata/Organisasi";
import DetailKaryawan from "./pages/admin/detail/DetailKaryawan";
import DetailLokasi from "./pages/admin/detail/DetailLokasi";
import Simpel from "./pages/admin/rekapan/simpel";
import DetailOrganisasi from "./pages/admin/detail/DetailOrganisasi";
import Login from "./pages/Login";
import {
  BrowserRouter,
  Route,
  Switch,
} from "react-router-dom/cjs/react-router-dom.min";
import Addkaryawan from "./pages/admin/add/Addkaryawan";
import AddJabatan from "./pages/admin/add/AddJabatan";
import AddShift from "./pages/admin/add/AddShift";
import AddLokasi from "./pages/admin/add/AddLokasi";
import AddOrganisasi from "./pages/admin/add/AddOrganisasi";
import EditKaryawan from "./pages/admin/edit/EditKaryawan";
import EditJabatan from "./pages/admin/edit/EditJabatan";
import EditLokasi from "./pages/admin/edit/EditLokasi";
import EditOrganisasi from "./pages/admin/edit/EditOrganisasi";
import Perkaryawan from "./pages/admin/rekapan/Perkaryawan";
import Harian from "./pages/admin/rekapan/Harian";
import Mingguan from "./pages/admin/rekapan/Mingguan";
import Bulanan from "./pages/admin/rekapan/Bulanan";
import Absensi from "./pages/admin/data absensi/Absensi";
import EditShift from "./pages/admin/edit/EditShift";
import Cuti from "./pages/admin/data absensi/Cuti";
import Kehadiran from "./pages/admin/data absensi/Kehadiran";
import Lembur from "./pages/admin/data absensi/Lembur";
import DashboardUser from "./pages/user/DashboardUser";
import DetailAbsen from "./pages/user/DetailAbsen";
import IzinAbsen from "./pages/user/IzinAbsen";
import Profile from "./pages/user/Profile";
import AbsenMasuk from "./pages/user/absen/AbsenMasuk";
import AbsenPulang from "./pages/user/absen/AbsenPulang";
import AddCuti from "./pages/user/add/AddCuti";
import AddIzin from "./pages/user/add/AddIzin";
import AddLembur from "./pages/user/add/AddLembur";
import TabelAbsen from "./pages/user/tabel/TabelAbsen";
import TabelCuti from "./pages/user/tabel/TabelCuti";
import TabelLembur from "./pages/user/tabel/TabelLembur";
import RegisterUser from "./pages/RegisterUser";
import DetailAbsensi from "./pages/admin/detail/DetailAbsensi";
import DashboardSA from "./pages/superadmin/DashboardSA";
import RegisterSuperadmin from "./pages/RegisterSuperadmin";
import Profil from "./pages/admin/Profil";
import Admin from "./pages/superadmin/admin/Admin";
import AddAdmin from "./pages/superadmin/add/AddAdmin";
import DetailAdmin from "./pages/superadmin/detail/DetailAdmin";
import EditAdmin from "./pages/superadmin/edit/EditAdmin";
import DetailLembur from "./pages/admin/detail/DetailLembur";
import OrganisasiSA from "./pages/superadmin/admin/OrganisasiSA";
import AddOrganisasiSA from "./pages/superadmin/add/AddOrganisasiSA";
import EditOrganisasiSA from "./pages/superadmin/edit/EditOrganisasiSA";
import ShiftSA from "./pages/superadmin/admin/ShiftSA";
import DetailShiftSA from "./pages/superadmin/detail/DetailShiftSA";
import AddShiftSA from "./pages/superadmin/add/AddShiftSA";
import EditShiftSA from "./pages/superadmin/edit/editShiftSA";
import JabatanSA from "./pages/superadmin/admin/JabatanSA";
import DetailJabatanSA from "./pages/superadmin/detail/DetailJabatanSA";
import EditJabatanSA from "./pages/superadmin/edit/EditJabatanSA";
import AddJabatanSA from "./pages/superadmin/add/AddJabatanSA";
import User from "./pages/superadmin/admin/User";
import AddUser from "./pages/superadmin/add/AddUser";
import EditUser from "./pages/superadmin/edit/EditUser";
import DetailUser from "./pages/superadmin/detail/DetailUser";
import LokasiSA from "./pages/superadmin/admin/LokasiSA";
import AddLokasiSA from "./pages/superadmin/add/AddLokasiSA";
import EditLokasiSA from "./pages/superadmin/edit/EditLokasiSA";
import DetailLokasiSA from "./pages/superadmin/detail/DetailLokasiSA";
import AbsensiSA from "./pages/superadmin/admin/AbsensiSA";
import DetailOrganisasiSA from "./pages/superadmin/detail/DetailOrganisasiSA";
import DetailAbsensiSA from "./pages/superadmin/detail/DetailAbsensiSA";
import ProfileSA from "./pages/superadmin/ProfilSA";
import ProfilSA from "./pages/superadmin/ProfilSA";
import ForgotPass from "./pages/ForgotPass";
import VerifyCode from "./pages/VerifyCode";
import ResetPassword from "./pages/ResetPassword";
import VerifyCodeSup from "./pages/superadmin/VerifyCodeSup";
import ResetPasswordSup from "./pages/superadmin/ReserPasswordSup";
import ForgotPassSup from "./pages/superadmin/ForgotpassSup";

function App() {
  const role = localStorage.getItem("role");
  return (
    <BrowserRouter>
      <main>
        <Switch>
          <Route path="/" component={Login} exact />
          <Route path="/register" component={Register} exact />
          <Route path="/registerUser" component={RegisterUser} exact />
          <Route path="/registerSA" component={RegisterSuperadmin} exact />
          <Route path="/forgotpass" component={ForgotPass} exact />
          <Route path="/verify-code" component={VerifyCode} exact/>
          <Route path="/reset-password/:token" component={ResetPassword} exact/>
          {/* superadmin */}
          <Route path="/forgotpassSup" component={ForgotPassSup} exact />
          <Route path="/verify-code-sup" component={VerifyCodeSup} exact/>
          <Route path="/reset-password-sup/:token" component={ResetPasswordSup} exact/>
          {/* start admin */}
          {/* Admin Routes */}
          {role === "ADMIN" && (
            <>
              <Route path="/admin/dashboard" component={Dashboard} exact />
              <Route path="/admin/profil" component={Profil} exact />
              {/* master data */}
              <Route path="/admin/karyawan" component={Karyawan} exact />
              <Route path="/admin/jabatan" component={Jabatan} exact />
              <Route path="/admin/shift" component={Shift} exact />
              <Route path="/admin/lokasi" component={Lokasi} exact />
              <Route path="/admin/organisasi" component={Organisasi} exact />
              <Route
                path="/admin/detailK/:id"
                component={DetailKaryawan}
                exact
              />
              <Route
                path="/admin/detailLembur/:id"
                component={DetailLembur}
                exact
              />
              <Route path="/admin/detailL/:id" component={DetailLokasi} exact />
              <Route
                path="/admin/detailO/:id"
                component={DetailOrganisasi}
                exact
              />
              <Route
                path="/admin/detailA/:id"
                component={DetailAbsensi}
                exact
              />
              <Route path="/admin/addkary" component={Addkaryawan} exact />
              <Route path="/admin/addjab" component={AddJabatan} exact />
              <Route path="/admin/addshift" component={AddShift} exact />
              <Route path="/admin/addlok" component={AddLokasi} exact />
              <Route path="/admin/addor" component={AddOrganisasi} exact />
              <Route path="/admin/editK/:id" component={EditKaryawan} exact />
              <Route path="/admin/editJ/:id" component={EditJabatan} exact />
              <Route path="/admin/editL/:id" component={EditLokasi} exact />
              <Route path="/admin/editO/:id" component={EditOrganisasi} exact />
              <Route path="/admin/editS/:id" component={EditShift} exact />
              {/* rekapan */}
              <Route path="/admin/simpel" component={Simpel} exact />
              <Route path="/admin/perkaryawan" component={Perkaryawan} exact />
              <Route path="/admin/harian" component={Harian} exact />
              <Route path="/admin/mingguan" component={Mingguan} exact />
              <Route path="/admin/bulanan" component={Bulanan} exact />
              {/* data absensi */}
              <Route path="/admin/absensi" component={Absensi} exact />
              <Route path="/admin/cuti" component={Cuti} exact />
              <Route path="/admin/kehadiran" component={Kehadiran} exact />
              <Route path="/admin/lembur" component={Lembur} exact />
            </>
          )}
          {/* end admin */}
          {/* /* start user */}
          {role === "USER" && (
            <>
              <Route path="/user/dashboard" component={DashboardUser} exact />
              <Route path="/user/history_absen" component={TabelAbsen} exact />
              <Route path="/user/history_cuti" component={TabelCuti} exact />
              <Route
                path="/user/history_lembur"
                component={TabelLembur}
                exact
              />
              <Route path="/user/cuti" component={AddCuti} exact />
              <Route path="/user/lembur" component={AddLembur} exact />
              <Route path="/user/izin" component={AddIzin} exact />
              <Route path="/user/profile" component={Profile} exact />
              <Route path="/user/absen" component={AbsenMasuk} exact />
              <Route path="/user/pulang" component={AbsenPulang} exact />
              <Route
                path="/user/detail_absen/:id"
                component={DetailAbsen}
                exact
              />
              <Route path="/user/izin_absen" component={IzinAbsen} exact />
            </>
          )}
          {/* end user */}
          {/* start superadmin */}
          {/* superadmin Routes */}
          {role === "SUPERADMIN" && (
            <>
              <Route
                path="/superadmin/dashboard"
                component={DashboardSA}
                exact
              />
              {/* admin */}
              <Route path="/superadmin/admin" component={Admin} exact />
              <Route path="/superadmin/addA" component={AddAdmin} exact />
              <Route
                path="/superadmin/detailA/:id"
                component={DetailAdmin}
                exact
              />
              <Route path="/superadmin/editA/:id" component={EditAdmin} exact />
              {/* organisasi */}
              <Route
                path="/superadmin/organisasi"
                component={OrganisasiSA}
                exact
              />
              <Route
                path="/superadmin/profile"
                component={ProfilSA}
                exact
              />
              <Route
                path="/superadmin/addO"
                component={AddOrganisasiSA}
                exact
              />
              <Route
                path="/superadmin/detailO/:id"
                component={DetailOrganisasiSA}
                exact
              />
              <Route
                path="/superadmin/editO/:id"
                component={EditOrganisasiSA}
                exact
              />
              {/* jabatan */}
              <Route path="/superadmin/jabatan" component={JabatanSA} exact />
              <Route path="/superadmin/addJ" component={AddJabatanSA} exact />
              <Route
                path="/superadmin/detailJ/:id"
                component={DetailJabatanSA}
                exact
              />
              <Route
                path="/superadmin/editJ/:idJabatan"
                component={EditJabatanSA}
                exact
              />
              {/* shift */}
              <Route path="/superadmin/shift" component={ShiftSA} exact />
              <Route
                path="/superadmin/detailS/:id"
                component={DetailShiftSA}
                exact
              />
              <Route
                path="/superadmin/add-shift"
                component={AddShiftSA}
                exact
              />
              <Route
                path="/superadmin/editS/:id"
                component={EditShiftSA}
                exact
              />
              <Route path="/superadmin/data-user" component={User} exact />
              <Route path="/superadmin/addU" component={AddUser} exact />
              <Route path="/superadmin/editU/:id" component={EditUser} exact />
              <Route
                path="/superadmin/detailU/:id"
                component={DetailUser}
                exact
              />
              <Route path="/superadmin/lokasi" component={LokasiSA} exact />
              <Route
                path="/superadmin/addLokasi"
                component={AddLokasiSA}
                exact
              />
              <Route
                path="/superadmin/editLokasi/:idLokasi"
                component={EditLokasiSA}
                exact
              />
              <Route
                path="/superadmin/detailLokasi/:idLokasi"
                component={DetailLokasiSA}
                exact
              />
              <Route path="/superadmin/absensi" component={AbsensiSA} exact />
              <Route
                path="/superadmin/detailAbsensi/:id"
                component={DetailAbsensiSA}
                exact
              />
            </>
          )}
          {/* end superadmin */}
        </Switch>
      </main>
    </BrowserRouter>
  );
}

export default App;
