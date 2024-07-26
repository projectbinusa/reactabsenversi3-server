import React, { useEffect, useState } from "react";
import Navbar from "../../../components/NavbarSuper";
import Sidebar from "../../../components/SidebarUser";
import { faArrowLeft, faFloppyDisk } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import Swal from "sweetalert2";
import { API_DUMMY } from "../../../utils/api";
function AddOrganisasiSA() {
  const [showPassword, setShowPassword] = useState(false);
  const [emailOrganisasi, setEmailOrganisasi] = useState("");
  const [namaOrganisasi, setnamaorganisasi] = useState("");
  const [alamat, setAlamat] = useState("");
  const [nomerTelepon, setNomerTelepon] = useState("");
  const [kecamatan, setKecamatan] = useState("");
  const [kabupaten, setKabupaten] = useState("");
  const [provinsi, setProvinsi] = useState("");
  const idSuperAdmin = localStorage.getItem("superadminId");
  const [organisasiList, setOrganisasiList] = useState([]);
  const [adminList, setAdminList] = useState([]);
  const [organisasi, setOrganisasi] = useState("");
  const [idAdmin, setAdminId] = useState("");

  const GetALLAdmin = async () => {
    try {
      const response = await axios.get(
        `${API_DUMMY}/api/admin/get-all-by-super/${idSuperAdmin}`
      );
      setAdminList(response.data);
    } catch (error) {
      console.log(error);
      Swal.fire("Error", "Gagal mendapatkan data admin", "error");
    }
  };

  const GetALLOrganisasi = async () => {
    try {
      const response = await axios.get(
        `${API_DUMMY}/api/admin/get-all-by-super/${idSuperAdmin}`
      );

      setOrganisasiList(response.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };
  const tambahAdmin = async (e) => {
    e.preventDefault();
    try {
      const organisasi = {
        namaOrganisasi: namaOrganisasi,
        alamat: alamat,
        kecamatan: kecamatan,
        kabupaten: kabupaten,
        provinsi: provinsi,
        nomerTelepon: nomerTelepon,
        emailOrganisasi: emailOrganisasi,
       };



      const response = await axios.post(
        `${API_DUMMY}/api/organisasi/tambahByIdSuperAdmin/${idSuperAdmin}?idAdmin=${idAdmin}`,
        organisasi,

      );
      Swal.fire("Berhasil", "Berhasil menambahkan data", "success");
      window.location.href = "/superadmin/organisasi";
    } catch (error) {
      console.log(error);
      Swal.fire("Error", "Gagal menambahkan data", "error");
    }
  };
  const handleShowPasswordChange = () => {
    setShowPassword(!showPassword);
  };
  useEffect(() => {
    GetALLOrganisasi();
    GetALLAdmin();
  }, []);
  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <Navbar />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar />
        </div>
      </div>
      <div className=" sm:ml-64 content-page p-8  ml-14 md:ml-64 mb-12">
        <div className="p-4">
          <div className="p-5">
            {/* // <!-- Card --> */}
            <div class="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
              {/* <!-- Header --> */}
              <div class="flex justify-between">
                <h6 class="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                  Tambah Organisasi
                </h6>
              </div>

              <hr />

              <div class="mt-5 text-left">
                {/* <!-- Form Input --> */}
                <form
                  onSubmit={tambahAdmin}
                  action="https://demo-absen.excellentsistem.com/superadmin/aksi_tambah_organisasi"
                  method="post"
                  enctype="multipart/form-data"
                >
                  <div class="grid md:grid-cols-2 md:gap-6">
                    {/* <!-- Organisasi Input --> */}
                    <div class="relative z-0 w-full mb-6 group">
                      <input
                        type="text"
                        name="nama_organisasi"
                        id="nama_organisasi"
                        value={namaOrganisasi}
                        onChange={(e) => setnamaorganisasi(e.target.value)}
                        class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                        placeholder=" "
                        autocomplete="off"
                        required
                      />
                      <label
                        for="nama_organisasi"
                        class="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                      >
                        Nama Organisasi
                      </label>
                    </div>

                    {/* <!-- Alamat Input --> */}
                    <div class="relative z-0 w-full mb-6 group">
                      <input
                        type="text"
                        name="alamat"
                        id="alamat"
                        class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                        placeholder=" "
                        autocomplete="off"
                        required
                        value={alamat}
                        onChange={(e) => setAlamat(e.target.value)}
                      />
                      <label
                        for="alamat"
                        class="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                      >
                        Alamat
                      </label>
                    </div>
                  </div>

                  <div class="grid md:grid-cols-2 md:gap-6">
                    {/* <!-- No Telepon Input --> */}
                    <div class="relative z-0 w-full mb-6 group">
                      <input
                        type="text"
                        name="nomor_telepon"
                        id="nomor_telepon"
                        class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                        placeholder=" "
                        autocomplete="off"
                        required
                        value={nomerTelepon}
                        onChange={(e) => setNomerTelepon(e.target.value)}
                      />
                      <label
                        for="nomor_telepon"
                        class="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                      >
                        No Telepon
                      </label>
                    </div>

                    {/* <!-- Email Input --> */}
                    <div class="relative z-0 w-full mb-6 group">
                      <input
                        type="email"
                        name="email_organisasi"
                        id="email_organisasi"
                        class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                        placeholder=" "
                        autocomplete="off"
                        required
                        value={emailOrganisasi}
                        onChange={(e) => setEmailOrganisasi(e.target.value)}
                      />
                      <label
                        for="email_organisasi"
                        class="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                      >
                        Email
                      </label>
                    </div>
                  </div>

                  <div class="grid md:grid-cols-2 md:gap-6">
                    {/* <!-- Kecamatan Input --> */}
                    <div class="relative z-0 w-full mb-6 group">
                      <input
                        type="text"
                        name="kecamatan"
                        id="kecamatan"
                        class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                        placeholder=" "
                        autocomplete="off"
                        required
                        value={kecamatan}
                        onChange={(e) => setKecamatan(e.target.value)}
                      />
                      <label
                        for="kecamatan"
                        class="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                      >
                        Kecamatan
                      </label>
                    </div>

                    {/* <!-- Kabupaten Input --> */}
                    <div class="relative z-0 w-full mb-6 group">
                      <input
                        type="text"
                        name="kabupaten"
                        id="kabupaten"
                        class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                        placeholder=" "
                        autocomplete="off"
                        required
                        value={kabupaten}
                        onChange={(e) => setKabupaten(e.target.value)}
                      />
                      <label
                        for="kabupaten"
                        class="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                      >
                        Kabupaten
                      </label>
                    </div>
                  </div>

                  <div class="grid md:grid-cols-2 md:gap-6">
                    {/* <!-- Provinsi Input --> */}
                    <div class="relative z-0 w-full mb-6 group">
                      <input
                        type="text"
                        name="provinsi"
                        id="provinsi"
                        class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                        placeholder=" "
                        autocomplete="off"
                        required
                        value={provinsi}
                        onChange={(e) => setProvinsi(e.target.value)}
                      />
                      <label
                        for="provinsi"
                        class="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                      >
                        Provinsi
                      </label>
                    </div>

                    {/* <!-- Admin Input --> */}
                    <div className="relative z-0 w-full mb-6 group">
                      <select
                        id="id_admin"
                        name="id_admin"
                        className="block py-2.5 px-0 w-full text-sm text-gray-500 bg-transparent border-0 border-b-2 border-gray-200 appearance-none dark:text-gray-400 dark:border-gray-700 focus:outline-none focus:ring-0 focus:border-gray-200 peer"
                        onChange={(e) => setAdminId(e.target.value)}
                        required
                      >
                        <option value="" disabled selected>
                          Pilih Admin
                        </option>
                        {adminList &&
                          adminList.map((admin) => (
                            <option key={admin.id} value={admin.id}>
                              {admin.username}
                            </option>
                          ))}
                      </select>
                    </div>
                  </div>

                  {/* <!-- Button --> */}
                  <div class="flex justify-between">
                    <a
                      class="focus:outline-none text-white bg-red-500 hover:bg-red-800 focus:ring-4 focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-red-600 dark:hover:bg-red-700 dark:focus:ring-red-900"
                      href="javascript:history.go(-1)"
                    >
                      <FontAwesomeIcon icon={faArrowLeft} />
                    </a>
                    <button
                      type="submit"
                      class="text-white bg-indigo-500 hover:bg-indigo-800 focus:ring-4 focus:ring-indigo-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-indigo-600 dark:hover:bg-indigo-700 focus:outline-none dark:focus:ring-indigo-800"
                    >
                      <FontAwesomeIcon icon={faFloppyDisk} />
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default AddOrganisasiSA;
