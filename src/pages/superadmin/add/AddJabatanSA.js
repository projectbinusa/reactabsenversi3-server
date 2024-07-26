import React, { useEffect, useState } from "react";
import Navbar from "../../../components/NavbarSuper";
import Sidebar from "../../../components/SidebarUser";
import { faArrowLeft, faFloppyDisk } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import axios from "axios";
import Swal from "sweetalert2";
import { useHistory } from "react-router-dom/cjs/react-router-dom";
import { API_DUMMY } from "../../../utils/api";

function AddJabatanSA() {
  const [showPassword, setShowPassword] = useState(false);
  const [namaJabatan, setNamaJabatan] = useState("");
  const idSuperAdmin = localStorage.getItem("superadminId");
  const [adminList, setAdminList] = useState([]);
  const [adminId, setadminId] = useState("");
  const history = useHistory();

  const tambahJabatan = async (e) => {
    e.preventDefault();
    try {
      const jabatan = {
        namaJabatan: namaJabatan,
      };
      const response = await axios.post(
        `${API_DUMMY}/api/jabatan/add/${adminId}`,
        jabatan
      );
      Swal.fire({
        title: "Berhasil",
        text: "Berhasil menambahkan jabatan",
        icon: "success",
        timer: 2000,
        showConfirmButton: false,
      });
      history.push("/superadmin/jabatan");
    } catch (error) {
      console.log(error);
      Swal.fire("Error", "Gagal menambahkan data", "error");
    }
  };
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
  useEffect(() => {
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
      <div className=" sm:ml-64 content-page p-8  ml-14 md:ml-64 mb-60">
        <div className="p-4">
          <div className="p-5">
            <div class="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
              {/* <!-- Header --> */}
              <div class="flex justify-between">
                <h6 class="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                  Tambah Jabatan
                </h6>
              </div>

              <hr />

              <div class="mt-5 text-left">
                {/* <!-- Form Input --> */}
                <form
                  onSubmit={tambahJabatan}
                >
                  <div class="grid md:grid-cols-2 md:gap-6">
                    {/* <!-- Nama Jabatan Input --> */}
                    <div class="relative z-0 w-full mb-6 group">
                      <input
                        type="text"
                        name="nama_jabatan"
                        id="nama_jabatan"
                        class="block py-2.5 px-0 w-full text-sm text-gray-900 bg-transparent border-0 border-b-2 border-gray-300 appearance-none dark:text-white dark:border-gray-600 dark:focus:border-blue-500 focus:outline-none focus:ring-0 focus:border-blue-600 peer"
                        placeholder=" "
                        autocomplete="off"
                        required
                        alue={namaJabatan}
                        onChange={(e) => setNamaJabatan(e.target.value)}
                      />
                      <label
                        for="nama_jabatan"
                        class="peer-focus:font-medium absolute text-sm text-gray-500 dark:text-gray-400 duration-300 transform -translate-y-6 scale-75 top-3 -z-10 origin-[0] peer-focus:left-0 peer-focus:text-blue-600 peer-focus:dark:text-blue-500 peer-placeholder-shown:scale-100 peer-placeholder-shown:translate-y-0 peer-focus:scale-75 peer-focus:-translate-y-6"
                      >
                        Nama Jabatan
                      </label>
                    </div>

                    {/* <!-- Admin Input --> */}
                    <div class="relative z-0 w-full mb-6 group">
                      {/* <!-- <label for="underline_select" class="sr-only">Underline select</label> --> */}
                      <select
                        id="id_admin"
                        name="id_admin"
                        value={adminId}
                        class="block py-2.5 px-0 w-full text-sm text-gray-500 bg-transparent border-0 border-b-2 border-gray-200 appearance-none dark:text-gray-400 dark:border-gray-700 focus:outline-none focus:ring-0 focus:border-gray-200 peer"
                        onChange={(e) => setadminId(e.target.value)}
                      >
                        <option selected>Pilih Admin</option>
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

export default AddJabatanSA;
